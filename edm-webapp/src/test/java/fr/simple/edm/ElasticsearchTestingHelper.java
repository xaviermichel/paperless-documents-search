package fr.simple.edm;

import fr.simple.edm.repository.EdmCategoryRepository;
import fr.simple.edm.repository.EdmDocumentRepository;
import fr.simple.edm.repository.EdmSourceRepository;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ElasticsearchTestingHelper {

    public static final String ES_INDEX_DOCUMENT_FILE = "document_file";
    public static final String ES_INDEX_SOURCE = "source";
    public static final String ES_INDEX_CATEGORY = "category";

    @Autowired
    private Client elasticsearchClient;

    @Autowired
    private EdmDocumentRepository edmDocumentRepository;

    @Autowired
    private EdmSourceRepository edmSourceRepository;

    @Autowired
    private EdmCategoryRepository edmCategoryRepository;

    private void deleteAllDocumentsForIndex(String index) throws Exception {
        if (ES_INDEX_DOCUMENT_FILE.equals(index)) {
            edmDocumentRepository.deleteAll();
        }
        else if (ES_INDEX_SOURCE.equals(index)) {
            edmSourceRepository.deleteAll();
        }
        else if (ES_INDEX_CATEGORY.equals(index)) {
            edmCategoryRepository.deleteAll();
        }
    }

    private void flushIndex(String index) throws Exception {
        elasticsearchClient.admin().indices().refresh(new RefreshRequest(index)).actionGet();
    }

    public void deleteAllDocuments() throws Exception {
        deleteAllDocumentsForIndex(ES_INDEX_DOCUMENT_FILE);
        deleteAllDocumentsForIndex(ES_INDEX_SOURCE);
        deleteAllDocumentsForIndex(ES_INDEX_CATEGORY);
    }

    public void flushIndexes() throws Exception {
        flushIndex(ES_INDEX_DOCUMENT_FILE);
        flushIndex(ES_INDEX_SOURCE);
        flushIndex(ES_INDEX_CATEGORY);
    }
}
