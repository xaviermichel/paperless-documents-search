package fr.simple.edm.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Base64;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder.Field;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.core.FacetedPageImpl;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import fr.simple.edm.ElasticsearchConfig;
import fr.simple.edm.model.EdmDocumentFile;
import fr.simple.edm.model.EdmDocumentSearchResult;
import fr.simple.edm.model.EdmNode;
import fr.simple.edm.model.EdmSource;
import fr.simple.edm.repository.EdmDocumentRepository;

@Service
@PropertySources(value = {
		@PropertySource("classpath:/edm-configuration.properties")
	}
)
public class EdmDocumentService {

	private final Logger logger = LoggerFactory.getLogger(EdmDocumentService.class);

	// html tag for highlighting matching result, for example :
	// "...this is a <mark>simple</mark> demo..."
	private static final String SEARCH_MATCH_HIGHLIHT_HTML_TAG = "mark";

	@Inject
	private Client elasticsearchClient;
	
	@Inject
	private EdmDocumentRepository edmDocumentRepository;

	@Inject
	private EdmNodeService edmNodeService;

	@Inject
	private EdmSourceService edmSourceService;
	
	@Inject
	private ElasticsearchOperations elasticsearchTemplate;

	@Inject
	private ElasticsearchConfig elasticsearchConfig;
	
	@Inject
	private Environment env;
	
	// Map<source, List<documentId>>, is used to delete removed document at re-indexation
	private static Map<String, List<String>> sourceDocumentsIds;

	static {
		sourceDocumentsIds = new HashMap<>();
	}

	public EdmDocumentFile findOne(String id) {
		return edmDocumentRepository.findOne(id);
	}

	public EdmDocumentFile save(EdmDocumentFile edmDocument) {

		// unique identifier for updating
        String id = DigestUtils.md5Hex(edmDocument.getNodePath() + "@" + edmDocument.getParentId());
		edmDocument.setId(id);
        
		try {
			// the document is build manually to
			// have the possibility to add the binary file
			// content

			XContentBuilder contentBuilder = jsonBuilder();

			// add document attributes
			contentBuilder.startObject();

			Class<?>[] classes = new Class[] { EdmNode.class, EdmDocumentFile.class };
			for (Class<?> clazz : classes) {
				for (Method m : clazz.getDeclaredMethods()) {
					if (m.getName().startsWith("get")) {
						if (m.getName().equalsIgnoreCase("getFilename")) { // ignore this type
							continue;
						}
						Object oo = m.invoke(edmDocument);
						String fieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, m.getName().substring(3));
						contentBuilder.field(fieldName, oo);
					}
				}
			}

			if (!edmDocument.getFilename().isEmpty()) {

				// computed values
				String thisDocumentFileExtension = com.google.common.io.Files.getFileExtension(edmDocument.getFilename());
				edmDocument.setFileExtension(thisDocumentFileExtension);

				String from = edmDocument.getFilename();
				edmDocument.setFilename("");
				Path filePath = Paths.get(from);

				contentBuilder.startObject("file");

				String contentType = Files.probeContentType(filePath);
				String content = Base64.encodeBytes(Files.readAllBytes(filePath));

				contentBuilder.field("_content", content);
				contentBuilder.field("_language", "fr");

				contentBuilder.endObject();

				contentBuilder.field("fileExtension", thisDocumentFileExtension);
				contentBuilder.field("fileContentType", contentType);

				// remove temporary document
				Files.deleteIfExists(filePath);
			}

			// and that's all folks
			contentBuilder.endObject();

			// TODO : dynamise index and type with EdmDocument annotation !
			IndexResponse ir = elasticsearchClient.prepareIndex("documents", "document_file", edmDocument.getId()).setSource(contentBuilder).execute().actionGet();

			edmDocument.setId(ir.getId());

			logger.debug("Indexed edm document '{}' with id '{}'", edmDocument.getName(), edmDocument.getId());
		} catch (Exception e) {
			logger.error("Failed to index document", e);
		}

		if (sourceDocumentsIds.get(edmDocument.getParentId()) != null) {
			sourceDocumentsIds.get(edmDocument.getParentId()).remove(edmDocument.getId());
		}
		
		return edmDocument;
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
	public List<EdmDocumentSearchResult> search(String pattern) {

		// basic query
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.queryString(pattern).defaultOperator(Operator.AND).field("name").field("description").field("file").field("nodePath"));
		logger.debug("The search query for pattern '{}' is : {}", pattern, qb);

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

		final List<EdmDocumentSearchResult> searchResult = new ArrayList<>();

		// Highlight result
		elasticsearchTemplate.queryForPage(searchQuery, EdmDocumentFile.class, new SearchResultMapper() {
			@Override
			public <T> FacetedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
				List<EdmDocumentFile> chunk = new ArrayList<>();
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

		// return modified result with highlighting
		return searchResult;
	}

	public List<EdmDocumentFile> findByParent(String parentId) {
		Page<EdmDocumentFile> page = edmDocumentRepository.findByParentId(parentId, new PageRequest(0, 99, new Sort(Sort.Direction.ASC, "name")));
		return page.getContent();
	}

	public List<EdmDocumentFile> findByName(String name) {
		return edmDocumentRepository.findByName(name);
	}

	public void delete(EdmDocumentFile edmDocument) {
		edmDocumentRepository.delete(edmDocument);
	}

	/**
	 * Convert the file path to a node path.
	 * 
	 * Actually, the idea is the file path has just document.fileExtension more
	 * than node path
	 */
	public String filePathToNodePath(String filePath) {
		return new File(filePath).getParent().replace("\\", "/") + "/" + com.google.common.io.Files.getNameWithoutExtension(filePath);
	}

	public EdmDocumentFile findEdmDocumentByFilePath(String filePath) {
		String nodePath = filePathToNodePath(filePath);
		logger.debug("Get server file path for node path : '{}'", nodePath);
		return (EdmDocumentFile) edmNodeService.findOneByPath(nodePath);
	}

	public void snapshotCurrentDocumentsForSource(String sourceName) {
		
		EdmSource source = edmSourceService.findOneByName(sourceName);
		if (source == null) {
			return;
		}
		String sourceId = source.getId();
		
		List<String> edmDocumentsIds = new ArrayList<>();

		int pageSize = 10;

		Pageable pageRequest = new PageRequest(0, pageSize);
		Page<EdmDocumentFile> edmDocumentPage = edmDocumentRepository.findByParentId(sourceId, pageRequest);

		while (edmDocumentPage.getSize() > 0) {
			logger.debug("EdmDocumentFile, findByParentId page {} on {}", edmDocumentPage.getNumber() + 1, edmDocumentPage.getTotalPages());

			for (EdmDocumentFile doc : edmDocumentPage.getContent()) {
				edmDocumentsIds.add(doc.getId());
			}

			if (!edmDocumentPage.hasNext()) {
				break;
			}
			pageRequest = edmDocumentPage.nextPageable();
			edmDocumentPage = edmDocumentRepository.findByParentId(sourceId, pageRequest);
		}
		sourceDocumentsIds.put(sourceId, edmDocumentsIds);
		
		logger.info("The snapshot contains {} documents for source {}", edmDocumentsIds.size(), source);
	}

	public void deleteUnusedDocumentsBeforeSnapshotForSource(String sourceName) {
		EdmSource source = edmSourceService.findOneByName(sourceName);
		if (source == null) {
			return;
		}
		String sourceId = source.getId();
		
		if (sourceDocumentsIds.get(sourceId) == null) {
			return;
		}
		
		logger.info("Will delete {} unused document(s) for source '{}'", sourceDocumentsIds.get(sourceId).size(), sourceId);
		// loop on removed documents
		for (String documentId : sourceDocumentsIds.get(sourceId)) {
			EdmDocumentFile edmDocumentFile = edmDocumentRepository.findOne(documentId);
			logger.debug("Delete document : {} ({})", edmDocumentFile.getNodePath(), edmDocumentFile.getId());
			delete(edmDocumentFile);
		}
		// reset map to be sur new ids won't be deleted
		sourceDocumentsIds.put(sourceId, new ArrayList<String>());
	}
	
	public List<EdmDocumentFile> getSuggestions(String wordPrefix) {
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		qb.must(QueryBuilders.queryString(wordPrefix).defaultOperator(Operator.OR).field("name.name_autocomplete").field("nodePath.nodePath_autocomplete"));
		logger.debug("The search query for pattern '{}' is : {}", wordPrefix, qb);
		return Lists.newArrayList(edmDocumentRepository.search(qb));
	}
	
	
	private List<String> getExtensions() {
		QueryBuilder query = QueryBuilders.matchAllQuery();
		TermsBuilder aggregationBuilder = AggregationBuilders.terms("agg_terms_fileExtension").field("fileExtension").size(20);

		SearchResponse response = elasticsearchConfig.getClient().prepareSearch("documents").setTypes("document_file")
                .setQuery(query)
                .addAggregation(aggregationBuilder)
                .execute().actionGet();
		
		List<String> extensions = new ArrayList<>();
		
		Terms terms = response.getAggregations().get("agg_terms_fileExtension");
		Collection<Terms.Bucket> buckets = terms.getBuckets();
		
		for (Terms.Bucket bucket : buckets) {
			extensions.add(bucket.getKey());
		}
		return extensions;
	}
	
	
	public List<String> getTopTerms() {
		QueryBuilder query = QueryBuilders.matchAllQuery();
		
		String userExclusionList = env.getProperty("edm.top_terms.exlusion_regex");
		String filesExtension = Joiner.on("|").join(getExtensions());
		
		TermsBuilder aggregationBuilder = AggregationBuilders.terms("agg_terms_nodePath").field("nodePath.nodePath_simple").exclude(userExclusionList + "|" + filesExtension);

		SearchResponse response = elasticsearchConfig.getClient().prepareSearch("documents").setTypes("document_file")
                .setQuery(query)
                .addAggregation(aggregationBuilder)
                .execute().actionGet();
		
		List<String> mostCommonTerms = new ArrayList<>();
		
		Terms terms = response.getAggregations().get("agg_terms_nodePath");
		Collection<Terms.Bucket> buckets = terms.getBuckets();
		
		for (Terms.Bucket bucket : buckets) {
			mostCommonTerms.add(bucket.getKey());
		}
		return mostCommonTerms;
	}
}
