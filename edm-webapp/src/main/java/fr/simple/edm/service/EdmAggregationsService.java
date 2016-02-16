package fr.simple.edm.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRange;
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeBuilder;
import org.elasticsearch.search.aggregations.bucket.range.date.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EdmAggregationsService {
    
    @Inject
    private EdmDocumentRepository edmDocumentRepository;
    
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

    public List<EdmDocumentFile> getSuggestions(String wordPrefix) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(QueryBuilders.queryStringQuery(wordPrefix).defaultOperator(Operator.OR).field("name.name_autocomplete").field("nodePath.nodePath_autocomplete"));
        log.debug("The search query for pattern '{}' is : {}", wordPrefix, qb);
        return Lists.newArrayList(edmDocumentRepository.search(qb));
    }

    private List<EdmAggregationItem> getAggregationExtensions(String relativeWordSearch) {
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);
        TermsBuilder aggregationBuilder = AggregationBuilders.terms("agg_fileExtension").field("fileExtension").size(20);

        List<EdmAggregationItem> extensions = new ArrayList<>();
        
        try {
            SearchResponse response = elasticsearchClient.prepareSearch("documents").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();
    
            Terms terms = response.getAggregations().get("agg_fileExtension");

            for (Terms.Bucket bucket : terms.getBuckets()) {
                extensions.add(new EdmAggregationItem(bucket.getKey(), bucket.getDocCount()));
            }
        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit getAggregationExtensions, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return extensions;
    }

    private List<EdmAggregationItem> getAggregationDate(String relativeWordSearch) {
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);
        DateRangeBuilder aggregationBuilder = AggregationBuilders.dateRange("agg_date").field("date");
        
        // last month
        aggregationBuilder.addUnboundedFrom("last_month", "now-1M/M");
        // last 2 months
        aggregationBuilder.addUnboundedFrom("last_2_months", "now-2M/M");
        // last 6 months
        aggregationBuilder.addUnboundedFrom("last_6_months", "now-6M/M");
        // last year
        aggregationBuilder.addUnboundedFrom("last_year", "now-12M/M");
        
        List<EdmAggregationItem> dates = new ArrayList<>();
        
        try {
            SearchResponse response = elasticsearchClient.prepareSearch("documents").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();

            InternalDateRange buckets = response.getAggregations().get("agg_date");
            
            for (DateRange.Bucket bucket : buckets.getBuckets()) {
                dates.add(new EdmAggregationItem(bucket.getKey(), bucket.getDocCount()));
            }
        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit getAggregationDate, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return dates;
    }

    public List<EdmAggregationItem> getTopTerms(String relativeWordSearch) {
        // the query
        QueryBuilder query = getEdmQueryForPattern(relativeWordSearch);

        List<String> filesExtensions = new ArrayList<>();
        for (EdmAggregationItem edmAggregationItem : getAggregationExtensions(null)) {
            filesExtensions.add(edmAggregationItem.getKey());
        }
        String filesExtension = StringUtils.join(filesExtensions, "|");

        TermsBuilder aggregationBuilder = AggregationBuilders.terms("agg_nodePath").field("nodePath.nodePath_simple").exclude(edmTopTermsExlusionRegex + "|" + filesExtension).size(10);

        List<EdmAggregationItem> mostCommonTerms = new ArrayList<>();

        try {
            // execute
            SearchResponse response = elasticsearchClient.prepareSearch("documents").setTypes("document_file")
                    .setQuery(query)
                    .addAggregation(aggregationBuilder)
                    .execute().actionGet();

            Terms terms = response.getAggregations().get("agg_nodePath");
            Collection<Terms.Bucket> buckets = terms.getBuckets();

            for (Terms.Bucket bucket : buckets) {
                mostCommonTerms.add(new EdmAggregationItem(bucket.getKey(), bucket.getDocCount()));
            }
        } catch (SearchPhaseExecutionException e) {
            log.warn("Failed to submit top terms, empty result ; may failed to parse relativeWordSearch ({}, more log to debug it !) : {}", e.getMessage(), relativeWordSearch);
        }

        return mostCommonTerms;
    }
}
