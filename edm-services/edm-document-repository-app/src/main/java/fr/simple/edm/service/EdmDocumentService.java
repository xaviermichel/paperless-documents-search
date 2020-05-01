package fr.simple.edm.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fr.simple.edm.annotation.EdmSearchable;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class EdmDocumentService {

	// html tag for highlighting matching result, for example :
	// "...this is a <mark>simple</mark> demo..."
	private static final String SEARCH_MATCH_HIGHLIGHT_HTML_TAG = "mark";

	private final EdmDocumentRepository edmDocumentRepository;

	private final Client elasticsearchClient;

	public EdmDocumentService(EdmDocumentRepository edmDocumentRepository, Client elasticsearchClient) {
		this.edmDocumentRepository = edmDocumentRepository;
		this.elasticsearchClient = elasticsearchClient;
	}

	public EdmDocumentFile findOne(String id) {
		return edmDocumentRepository.findById(id).get();
	}

	public EdmDocumentFile save(EdmDocumentFile edmDocument) {
		return edmDocumentRepository.save(edmDocument);
	}

	/**
	 * When you search a document, this query is executed
	 *
	 * @param pattern The searched pattern
	 * @return The adapted query
	 */
	QueryBuilder getEdmQueryForPattern(String pattern) {
		// in case of invalid query
		if (StringUtils.isEmpty(pattern)) {
			return QueryBuilders.matchAllQuery();
		}

		QueryStringQueryBuilder queryBuilder = QueryBuilders
				.queryStringQuery(pattern)
				.type(Type.CROSS_FIELDS)
				.defaultOperator(Operator.AND);

		List<String> searchableFieldsNames = Stream
				.of(EdmDocumentFileDto.class.getDeclaredFields())
				.filter(f -> f.isAnnotationPresent(EdmSearchable.class))
				.map(f -> f.getName())
				.collect(toList());

		for (String fieldName : searchableFieldsNames) {
			queryBuilder = queryBuilder.field(fieldName);
		}

		// the real query
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		return qb.must(queryBuilder);
	}

	/**
	 * Search from web UI
	 * Will color results
	 */
	public EdmDocumentSearchResultWrapper search(String pattern) {

		// basic query
		QueryBuilder qb = getEdmQueryForPattern(pattern);
		log.debug("The search query for pattern '{}' is : {}", pattern, qb);

		HighlightBuilder highlightBuilder = new HighlightBuilder()
				.preTags("<" + SEARCH_MATCH_HIGHLIGHT_HTML_TAG + ">")
				.postTags("</" + SEARCH_MATCH_HIGHLIGHT_HTML_TAG + ">")
				.field("name").field("fileContent").field("nodePath");


		final EdmDocumentSearchResultWrapper searchResult = new EdmDocumentSearchResultWrapper();

		try {
			SearchResponse searchResponse = elasticsearchClient.prepareSearch("document_file").setTypes("document_file")
					.setQuery(qb)
					.highlighter(highlightBuilder)
					.execute().actionGet();

			searchResult.setTookTime(searchResponse.getTook().getMillis());
			searchResult.setTotalHitsCount(searchResponse.getHits().getTotalHits());

			SearchHits hits = searchResponse.getHits();
			for (SearchHit searchHit : hits.getHits()) {
				EdmDocumentSearchResult edmDocumentSearchResult = new EdmDocumentSearchResult();

				// fill every fields
				EdmDocumentFile doc = edmDocumentRepository.findById(searchHit.getId()).get();
				edmDocumentSearchResult.setEdmDocument(doc);

				// add custom highlighted fields
				Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();

				if (highlightFields.get("name") != null) {
					edmDocumentSearchResult.setHighlightedName(
							Arrays.stream(highlightFields.get("name").fragments())
									.map(t -> t.string().trim())
									.collect(Collectors.joining("\n<i>[...]</i>\n"))
					);
				}

				if (highlightFields.get("fileContent") != null) {
					edmDocumentSearchResult.setHighlightedFileContentMatching(
							Arrays.stream(highlightFields.get("fileContent").fragments())
									.map(t -> t.string().trim())
									.collect(Collectors.joining("\n<i>[...]</i>\n"))
					);
				}

				if (highlightFields.get("nodePath") != null) {
					edmDocumentSearchResult.setHighlightedNodePath(
							Arrays.stream(highlightFields.get("nodePath").fragments())
									.map(t -> t.string().trim())
									.collect(Collectors.joining("\n<i>[...]</i>\n"))
					);
				}

				searchResult.add(edmDocumentSearchResult);
			}
		}
		catch (SearchPhaseExecutionException e) {
			log.warn("Failed to submit query, empty result ; may failed to parse query ({}, more log to debug it !) : {}", e.getMessage(), pattern);
		}

		// return modified result with highlighting
		return searchResult;
	}

}

