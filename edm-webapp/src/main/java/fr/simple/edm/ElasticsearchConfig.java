package fr.simple.edm;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.Charsets;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

@Configuration
@EnableElasticsearchRepositories(basePackages = "fr.simple.edm.repository")
@PropertySources(value = { 
        @PropertySource("classpath:/elasticsearch-configuration.properties")
})
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
	Environment env;

	private Client client;

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		// local case
		if (env.getProperty("edm.embedded-storage").equalsIgnoreCase("true")) {
			client = localClient();

			// on a local environment, we wan't to be sure the mapping is
			// applied
			client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
			buildEsMapping();

			return new ElasticsearchTemplate(client);
		}

		// remote case, don't care about mapping
		client = remoteClient();
		return new ElasticsearchTemplate(client);
	}

	// should be used with caution, only if you really need it for manual query
	public Client getClient() {
		return client;
	}

	private Client localClient() {
		logger.info("ES is using a local node");
		return NodeBuilder.nodeBuilder().local(true).node().client();
	}

	private Client remoteClient() {
		String remoteNodes = env.getProperty("elasticsearch.cluster-nodes");
		logger.info("ES is using remote nodes : {}", remoteNodes);

		TransportClient client = new TransportClient();
		for (String clusterNode : remoteNodes.split(",")) {
			String hostAndPort[] = clusterNode.split(":");
			String address = hostAndPort[0];
			Integer port = Integer.parseInt(hostAndPort[1]);
			logger.info("Add transport address {} , port {}", address, port);
			client.addTransportAddress(new InetSocketTransportAddress(address, port));
		}
		return client;
	}

	private void flushIndex(String index) {
		client.admin().indices().refresh(new RefreshRequest(index)).actionGet();
	}

	private void buildEsMapping() {

		logger.info("Trying to build ES mapping");

		Map<String, List<String>> indexTypes = new HashMap<>();
        indexTypes.put("documents", Lists.newArrayList("category", "source", "document_file"));

        for (String index : indexTypes.keySet()) {

        	try {
        		client.admin().indices().prepareCreate(index).execute().actionGet();
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
        			client.admin().indices().preparePutMapping(index).setType(type).setSource(content).execute().actionGet();
        		} catch (Exception e) {
        			logger.error("Failed to read mapping or update mapping for ES", e);
        			logger.error("THE MODE IS DETERIORED, YOU MAY HAVE TO MANUALY UPDATE MAPPING ! (see EdmAdministrationController)");
        		}
        	}

        	flushIndex(index);
        }

		logger.info("Building is over !");
	}

}
