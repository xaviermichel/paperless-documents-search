package fr.simple.edm;

import fr.simple.edm.repository.EdmDocumentRepository;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ElasticsearchTestingHelper {

    public static final String ES_INDEX_DOCUMENTS = "documents";

    @Autowired
    private Client elasticsearchClient;

    @Autowired
    private EdmDocumentRepository edmDocumentRepository;

    /**
     * Will destroy and rebuild ES_INDEX_DOCUMENTS
     */
    public void deleteAllDocumentsForIndex(String index) throws Exception {
    	if (ES_INDEX_DOCUMENTS.equals(index)) {
    		edmDocumentRepository.deleteAll();
    	}
    }


    public void flushIndex(String index) throws Exception  {
    	elasticsearchClient.admin().indices().refresh(new RefreshRequest(index)).actionGet();
    }
}
