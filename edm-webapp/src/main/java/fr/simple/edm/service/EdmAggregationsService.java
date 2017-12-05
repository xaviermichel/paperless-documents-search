package fr.simple.edm.service;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
@Slf4j
public class EdmAggregationsService {

    private static final int TOP_TERMS_MAX_COUNT = 10;
    private static final int FILE_EXTENSIONS_MAX_COUNT = 20;

    @Inject
    private EdmDocumentRepository edmDocumentRepository;

    @Inject
    private EdmDocumentService edmDocumentService;

    @Inject
    private Client elasticsearchClient;

    @Value("${edm.top_terms.exlusion_regex}")
    private String edmTopTermsExlusionRegex;


    public Map<String, List<EdmAggregationItem>> getAggregations(String pattern) {
        Map<String, List<EdmAggregationItem>> aggregations = new HashMap<>();
        aggregations.put("fileExtension", getAggregationExtensions(pattern));
        aggregations.put("date", getAggregationDate(pattern));
        return aggregations;
    }

    /**
     * When you search a document, this query is executed
     *
     * @param pattern The searched pattern
     * @return The adapted query
     */
    private QueryBuilder getEdmQueryForPattern(String pattern) {
        return edmDocumentService.getEdmQueryForPattern(pattern);
    }

    public List<EdmDocumentFile> getSuggestions(String wordPrefix) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(QueryBuilders.queryStringQuery(wordPrefix).defaultOperator(Operator.OR)
                .field("name.name_autocomplete").field("nodePath.nodePath_autocomplete")
        );
        log.debug("The search query for pattern '{}' is : {}", wordPrefix, qb);
        return createStreamFromIterator(edmDocumentRepository.search(qb).iterator()).collect(toList());
    }

    private List<EdmAggregationItem> getAggregationExtensions(String relativeWordSearch) {
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("agg_fileExtension").field("fileExtension").size(FILE_EXTENSIONS_MAX_COUNT);

        try {
            SearchResponse response = elasticsearchClient.prepareSearch("document_file").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();

            Terms terms = response.getAggregations().get("agg_fileExtension");

            return terms.getBuckets().stream()
                    .map(
                            bucket -> new EdmAggregationItem(bucket.getKeyAsString(), bucket.getDocCount())
                    )
                    .collect(toList());

        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit getAggregationExtensions, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return new ArrayList<>();
    }

    private List<EdmAggregationItem> getAggregationDate(String relativeWordSearch) {
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);
        DateRangeAggregationBuilder aggregationBuilder = AggregationBuilders.dateRange("agg_date").field("date");

        // last month
        aggregationBuilder.addUnboundedFrom("last_month", "now-1M/M");
        // last 2 months
        aggregationBuilder.addUnboundedFrom("last_2_months", "now-2M/M");
        // last 6 months
        aggregationBuilder.addUnboundedFrom("last_6_months", "now-6M/M");
        // last year
        aggregationBuilder.addUnboundedFrom("last_year", "now-12M/M");

        try {
            SearchResponse response = elasticsearchClient.prepareSearch("document_file").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();

            InternalDateRange buckets = response.getAggregations().get("agg_date");

            return buckets.getBuckets().stream()
                    .map(
                            bucket -> new EdmAggregationItem(bucket.getKeyAsString(), bucket.getDocCount())
                    )
                    .collect(toList());

        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit getAggregationDate, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return new ArrayList<>();
    }

    public List<EdmAggregationItem> getTopTerms(String relativeWordSearch) {
        // the query
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);

        String filesExtensions = getAggregationExtensions(null).stream()
                .map(edmAggregationItem -> edmAggregationItem.getKey())
                .collect(joining("|"));

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("agg_nodePath").field("nodePath.nodePath_simple").executionHint(edmTopTermsExlusionRegex + "|" + filesExtensions).size(TOP_TERMS_MAX_COUNT);

        try {
            // execute
            SearchResponse response = elasticsearchClient.prepareSearch("document_file").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();

            Terms terms = response.getAggregations().get("agg_nodePath");

            return terms.getBuckets().stream()
                    .map(
                            bucket -> new EdmAggregationItem(bucket.getKeyAsString(), bucket.getDocCount())
                    )
                    .collect(toList());

        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit top terms, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return new ArrayList<>();
    }
}
