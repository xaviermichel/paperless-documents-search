package fr.simple.edm.service;

import fr.simple.edm.annotation.EdmSearchable;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.repository.EdmDocumentRepository;
import fr.simple.edm.tika.EdmOcrDocExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class EdmDocumentService {

    // html tag for highlighting matching result, for example :
    // "...this is a <mark>simple</mark> demo..."
    private static final String SEARCH_MATCH_HIGHLIGHT_HTML_TAG = "mark";

    @Inject
    private EdmOcrDocExtractor edmOcrDocExtractor;

    @Inject
    private EdmDocumentRepository edmDocumentRepository;

    @Inject
    private Client elasticsearchClient;

    public EdmDocumentFile findOne(String id) {
        return edmDocumentRepository.findById(id).get();
    }

    public EdmDocumentFile save(EdmDocumentFile edmDocument) {

        // unique identifier for updating
        String id = DigestUtils.md5Hex(edmDocument.getNodePath() + "@" + edmDocument.getSourceId());
        edmDocument.setId(id);

        // read the file content
        edmOcrDocExtractor.extractFileContent(edmDocument);

        // force not index of binary content
        edmDocument.setBinaryFileContent(null);

        edmDocument = edmDocumentRepository.save(edmDocument);
        return edmDocument;
    }

    /**
     * When you search a document, this query is executed
     *
     * @param pattern The searched pattern
     * @return The adapted query
     */
    QueryBuilder getEdmQueryForPattern(String pattern) {
        // in case of invalid query
        if (StringUtils.isBlank(pattern)) {
            return QueryBuilders.matchAllQuery();
        }

        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(pattern).defaultOperator(Operator.AND);

        List<String> searchableFieldsNames = Stream
                .of(EdmDocumentFile.class.getDeclaredFields())
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

            searchResult.setTookTime(searchResponse.getTookInMillis());
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
                                    .map(t -> t.string())
                                    .collect(Collectors.joining("\n\n"))
                    );
                }

                if (highlightFields.get("fileContent") != null) {
                    edmDocumentSearchResult.setHighlightedFileContentMatching(
                            Arrays.stream(highlightFields.get("fileContent").fragments())
                                    .map(t -> t.string())
                                    .collect(Collectors.joining("\n\n"))
                    );
                }

                if (highlightFields.get("nodePath") != null) {
                    edmDocumentSearchResult.setHighlightedNodePath(
                            Arrays.stream(highlightFields.get("nodePath").fragments())
                                    .map(t -> t.string())
                                    .collect(Collectors.joining("\n\n"))
                    );
                }

                searchResult.add(edmDocumentSearchResult);
            }
        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit query, empty result ; may failed to parse query ({}, more log to debug it !) : {}", e.getMessage(), pattern);
        }

        // return modified result with highlighting
        return searchResult;
    }
}
