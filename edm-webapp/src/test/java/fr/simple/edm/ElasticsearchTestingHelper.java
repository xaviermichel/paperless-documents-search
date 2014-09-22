package fr.simple.edm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.simple.edm.ElasticsearchConfig;


@Component
public class ElasticsearchTestingHelper {

	
	public static final String ES_INDEX_DOCUMENTS = "documents";
	
	
	@Autowired
	public ElasticsearchConfig elasticsearchConfig;
	
	
	private Method flushIndexMethod;
	
	
	/**
	 * Will destroy and rebuild ES_INDEX_DOCUMENTS
	 */
	public void destroyAndRebuildIndex(String index) throws Exception {
		Field clientField = ElasticsearchConfig.class.getDeclaredField("client");
		clientField.setAccessible(true);

		Client client = (Client) clientField.get(elasticsearchConfig);
		
		Method rebuildEsMappingMethod = ElasticsearchConfig.class.getDeclaredMethod("buildEsMapping");
		rebuildEsMappingMethod.setAccessible(true);

		flushIndexMethod = ElasticsearchConfig.class.getDeclaredMethod("flushIndex", String.class);
		flushIndexMethod.setAccessible(true);
		
		client.admin().indices().delete(new DeleteIndexRequest(ES_INDEX_DOCUMENTS)).actionGet();
		rebuildEsMappingMethod.invoke(elasticsearchConfig);
	}
	
	
	public void flushIndex(String index) throws Exception  {
		flushIndexMethod.invoke(elasticsearchConfig, index);
	}
	
}
