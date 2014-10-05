package fr.simple.edm;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.Charsets;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

@EnableElasticsearchRepositories(basePackages = "fr.simple.edm.repository")
@Service
public class ElasticsearchConfig {

	/**
	 * My logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

	/**
	 * Mapping files location
	 */
	private static final String MAPPING_DIR = "./mapping";
	
	@Inject
	private Client elasticsearchClient;

	private void flushIndex(String index) {
		elasticsearchClient.admin().indices().refresh(new RefreshRequest(index)).actionGet();
	}

	private void buildOrUpdateEsMapping() {

		logger.info("Updating ES mapping because you're using a local node");

		Map<String, List<String>> indexTypes = new HashMap<>();
        indexTypes.put("documents", Lists.newArrayList("category", "source", "document_file"));

        for (String index : indexTypes.keySet()) {

        	try {
        		elasticsearchClient.admin().indices().prepareCreate(index).execute().actionGet();
        	} catch (IndexAlreadyExistsException e) {
        		logger.info("Index {} already exists", index);
        	} catch (Exception e) {
        		logger.error("Failed to rebuild index {}", index, e);
        	}

        	// at the second level, we've types
        	for (String type : indexTypes.get(index)) {
        		try {
        			URL url = Resources.getResource(MAPPING_DIR + "/" + index + "/" + type + ".json");
        			String content = Resources.toString(url, Charsets.UTF_8);
        			
        			logger.info("Updating mapping for {}/{}", index, type);
        			elasticsearchClient.admin().indices().preparePutMapping(index).setType(type).setSource(content).execute().actionGet();
        		} catch (Exception e) {
        			logger.error("Failed to read mapping or update mapping for ES", e);
        			logger.error("THE MODE IS DETERIORED, YOU MAY HAVE TO MANUALY UPDATE MAPPING ! (see EdmAdministrationController)");
        		}
        	}

        	flushIndex(index);
        }

		logger.info("Building is over !");
	}

	public void updateMappingIfLocalNode() {
		logger.info("Wan't update mapping...");
		if (elasticsearchClient != null && elasticsearchClient instanceof NodeClient) {
			buildOrUpdateEsMapping();
			return;
		}
		logger.info("It's not a local node, I won't edit mapping");
	}

}
