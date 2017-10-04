package fr.simple.edm.service;

import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import javax.persistence.Transient;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
@Slf4j
public class EdmDocumentService {

    // html tag for highlighting matching result, for example :
    // "...this is a <mark>simple</mark> demo..."
    private static final String SEARCH_MATCH_HIGHLIGHT_HTML_TAG = "mark";

    @Inject
    private Client elasticsearchClient;

    @Inject
    private EdmDocumentRepository edmDocumentRepository;

    @Inject
    private ElasticsearchOperations elasticsearchTemplate;


    public EdmDocumentFile findOne(String id) {
        return edmDocumentRepository.findById(id).get();
    }

    public EdmDocumentFile save(EdmDocumentFile edmDocument) {

        // unique identifier for updating
        String id = DigestUtils.md5Hex(edmDocument.getNodePath() + "@" + edmDocument.getSourceId());
        edmDocument.setId(id);

        try {
            // the document is build manually to
            // have the possibility to add the binary file
            // content

            XContentBuilder contentBuilder = jsonBuilder();

            // add document attributes
            contentBuilder.startObject();

            for (Method m : EdmDocumentFile.class.getDeclaredMethods()) {
                if (m.getName().startsWith("get")) {
                    Object oo = m.invoke(edmDocument);
                    String fieldName = WordUtils.uncapitalize(m.getName().substring(3));

                    // ignore if transient
                    if (EdmDocumentFile.class.getDeclaredField(fieldName).isAnnotationPresent(Transient.class)) {
                        continue;
                    }

                    contentBuilder.field(fieldName, oo);
                }
            }

			if (edmDocument.getFileContent() != null) {
				contentBuilder.field("fileContent", Base64Utils.encode(edmDocument.getFileContent()));
			}

            // and that's all folks
            contentBuilder.endObject();

            IndexResponse ir = elasticsearchClient.prepareIndex("documents", "document_file", edmDocument.getId())
                .setSource(contentBuilder).setPipeline("attachment").execute().actionGet();

            edmDocument.setId(ir.getId());

            log.debug("Indexed edm document '{}' with id '{}'", edmDocument.getName(), edmDocument.getId());
        } catch (Exception e) {
            log.error("Failed to index document", e);
        }

        return edmDocument;
    }

    /**
     * When you search a document, this query is executed
     *
     * @param pattern
     *            The searched pattern
     * @return The adapted query
     */
    private QueryBuilder getEdmQueryForPattern(String pattern) {
        // in case of invalid query
        if (StringUtils.isBlank(pattern)) {
            return QueryBuilders.matchAllQuery();
        }

        // the real query
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(QueryBuilders.queryStringQuery(pattern).defaultOperator(Operator.AND)
            .field("name").field("description").field("file.content").field("nodePath")
        );
        return qb;
    }

    /**
     * Search from web UI
     * Will color results
     */
    public EdmDocumentSearchResultWrapper search(String pattern) {

        // basic query
        QueryBuilder qb = getEdmQueryForPattern(pattern);
        log.debug("The search query for pattern '{}' is : {}", pattern, qb);

        // custom query for highlight
        String preTag = "<" + SEARCH_MATCH_HIGHLIGHT_HTML_TAG + ">";
        String postTag = "</" + SEARCH_MATCH_HIGHLIGHT_HTML_TAG + ">";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .withHighlightFields(
                    new HighlightBuilder.Field("name").preTags(preTag).postTags(postTag),
                    new HighlightBuilder.Field("description").preTags(preTag).postTags(postTag),
                    new HighlightBuilder.Field("file.content").preTags(preTag).postTags(postTag),
                    new HighlightBuilder.Field("nodePath").preTags(preTag).postTags(postTag)
                )
                .withSort(new ScoreSortBuilder())
                .build();

        final EdmDocumentSearchResultWrapper searchResult = new EdmDocumentSearchResultWrapper();

        try {
            // Highlight result
            elasticsearchTemplate.queryForPage(searchQuery, EdmDocumentFile.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    List<EdmDocumentFile> chunk = new ArrayList<>();

                    searchResult.setTookTime(response.getTookInMillis());
                    searchResult.setTotalHitsCount(response.getHits().getTotalHits());

                    for (SearchHit searchHit : response.getHits()) {
                        if (response.getHits().getHits().length <= 0) {
                            return new AggregatedPageImpl<T>((List<T>) chunk);
                        }

                        EdmDocumentSearchResult edmDocumentSearchResult = new EdmDocumentSearchResult();

                        // fill every fields
                        EdmDocumentFile doc = edmDocumentRepository.findById(searchHit.getId()).get();
                        edmDocumentSearchResult.setEdmDocument(doc);

                        // override custom elements, see
                        // https://groups.google.com/forum/#!topic/spring-data-elasticsearch-devs/se3yCfVnRiE
                        if (searchHit.getHighlightFields().get("name") != null) {
                            edmDocumentSearchResult.setHighlightedName(searchHit.getHighlightFields().get("name").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("description") != null) {
                            edmDocumentSearchResult.setHighlightedDescription(searchHit.getHighlightFields().get("description").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("file.content") != null) {
                            edmDocumentSearchResult.setHighlightedFileContentMatching(searchHit.getHighlightFields().get("file.content").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("nodePath") != null) {
                            edmDocumentSearchResult.setHighlightedNodePath(searchHit.getHighlightFields().get("nodePath").fragments()[0].toString());
                        }

                        searchResult.add(edmDocumentSearchResult);
                        chunk.add(doc);
                    }
                    return new AggregatedPageImpl<T>((List<T>) chunk);
                }
            });

        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit query, empty result ; may failed to parse query ({}, more log to debug it !) : {}", e.getMessage(), pattern);
        }

        // return modified result with highlighting
        return searchResult;
    }
}
