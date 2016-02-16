package fr.simple.edm.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.FacetedPageImpl;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EdmDocumentService {
    
    // html tag for highlighting matching result, for example :
    // "...this is a <mark>simple</mark> demo..."
    private static final String SEARCH_MATCH_HIGHLIHT_HTML_TAG = "mark";

    @Inject
    private Client elasticsearchClient;

    @Inject
    private EdmDocumentRepository edmDocumentRepository;
    
    @Inject
    private ElasticsearchOperations elasticsearchTemplate;


    public EdmDocumentFile findOne(String id) {
        return edmDocumentRepository.findOne(id);
    }

    public EdmDocumentFile save(EdmDocumentFile edmDocument) {

        // unique identifier for updating
        String id = DigestUtils.md5Hex(edmDocument.getNodePath() + "@" + edmDocument.getSourceId());
        edmDocument.setId(id);

        edmDocument.setCategoryId(edmDocument.getCategoryId());

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

            if (edmDocument.getFileContent() != null && edmDocument.getFileContent().length > 0) {
                contentBuilder.startObject("file");
                contentBuilder.field("_content", Base64.encodeBytes(edmDocument.getFileContent()));
                contentBuilder.field("_language", "fr");
                contentBuilder.endObject();
            }

            // and that's all folks
            contentBuilder.endObject();

            IndexResponse ir = elasticsearchClient.prepareIndex("documents", "document_file", edmDocument.getId()).setSource(contentBuilder).execute().actionGet();

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
        qb.must(QueryBuilders.queryStringQuery(pattern).defaultOperator(Operator.AND).field("name").field("description").field("file").field("nodePath"));
        return qb;
    }

    /*
        curl -XPOST 'http://127.0.0.1:9253/documents/document/_search?pretty=true' -d '
        {
           "fields":[

           ],
           "query":{
              "query_string":{
                 "query":"trololo",
                 "default_operator":"and"
              }
           },
           "highlight":{
              "fields":{
                 "file":{
                 },
                 "name":{
                 },
                 "description":{
                 },
                 "nodePath":{
                 }
              }
           }
        }
     */
    public EdmDocumentSearchResultWrapper search(String pattern) {

        // basic query
        QueryBuilder qb = getEdmQueryForPattern(pattern);
        log.debug("The search query for pattern '{}' is : {}", pattern, qb);

        // custom query for highlight
        String preTag = "<" + SEARCH_MATCH_HIGHLIHT_HTML_TAG + ">";
        String postTag = "</" + SEARCH_MATCH_HIGHLIHT_HTML_TAG + ">";
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .withHighlightFields(
                        new Field("name").preTags(preTag).postTags(postTag),
                        new Field("description").preTags(preTag).postTags(postTag),
                        new Field("file").preTags(preTag).postTags(postTag),
                        new Field("nodePath").preTags(preTag).postTags(postTag)
                )
                .withSort(new ScoreSortBuilder())
                .build();

        final EdmDocumentSearchResultWrapper searchResult = new EdmDocumentSearchResultWrapper();

        try {
            // Highlight result
            elasticsearchTemplate.queryForPage(searchQuery, EdmDocumentFile.class, new SearchResultMapper() {
                @Override
                public <T> FacetedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                    List<EdmDocumentFile> chunk = new ArrayList<>();

                    searchResult.setTookTime(response.getTookInMillis());
                    searchResult.setTotalHitsCount(response.getHits().getTotalHits());

                    for (SearchHit searchHit : response.getHits()) {
                        if (response.getHits().getHits().length <= 0) {
                            return new FacetedPageImpl<T>((List<T>) chunk);
                        }

                        EdmDocumentSearchResult edmDocumentSearchResult = new EdmDocumentSearchResult();

                        // fill every fields
                        EdmDocumentFile doc = edmDocumentRepository.findOne(searchHit.getId());
                        edmDocumentSearchResult.setEdmDocument(doc);

                        // override custom elements, see
                        // https://groups.google.com/forum/#!topic/spring-data-elasticsearch-devs/se3yCfVnRiE
                        if (searchHit.getHighlightFields().get("name") != null) {
                            edmDocumentSearchResult.setHighlightedName(searchHit.getHighlightFields().get("name").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("description") != null) {
                            edmDocumentSearchResult.setHighlightedDescription(searchHit.getHighlightFields().get("description").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("file") != null) {
                            edmDocumentSearchResult.setHighlightedFileContentMatching(searchHit.getHighlightFields().get("file").fragments()[0].toString());
                        }
                        if (searchHit.getHighlightFields().get("nodePath") != null) {
                            edmDocumentSearchResult.setHighlightedNodePath(searchHit.getHighlightFields().get("nodePath").fragments()[0].toString());
                        }

                        searchResult.add(edmDocumentSearchResult);
                        chunk.add(doc);
                    }
                    return new FacetedPageImpl<T>((List<T>) chunk);
                }
            });

        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit query, empty result ; may failed to parse query ({}, more log to debug it !) : {}", e.getMessage(), pattern);
        }

        // return modified result with highlighting
        return searchResult;
    }
}
