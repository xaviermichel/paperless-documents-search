package fr.simple.edm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Component;

import fr.simple.edm.util.ResourceUtils;

@EnableElasticsearchRepositories(basePackages = "fr.simple.edm.repository")
@Component
@Slf4j
public class ElasticsearchConfig {

    /**
     * Mapping files location
     */
    private static final String MAPPING_DIR = "./mapping";
    
    @Inject
    private Client elasticsearchClient;
    
    public Client getClient() {
        return elasticsearchClient;
    }
    
    
    private void flushIndex(String index) {
        elasticsearchClient.admin().indices().refresh(new RefreshRequest(index)).actionGet();
    }
    
    private void buildOrUpdateEsMapping() {

        log.info("Updating ES mapping because you're using a local node");

        Map<String, List<String>> indexTypes = new HashMap<>();
        indexTypes.put("documents", Arrays.asList("category", "source", "document_file"));
        
        for (String index : indexTypes.keySet()) {

            // create index, with settings if exists
            String indexSettings = ResourceUtils.getContentOfEmbeddedFile(MAPPING_DIR + "/" + index + ".json");
            try {
                if (indexSettings == null) {
                    elasticsearchClient.admin().indices().prepareCreate(index)/*************************/.execute().actionGet();
                } else {
                    elasticsearchClient.admin().indices().prepareCreate(index).setSettings(indexSettings).execute().actionGet();
                }
            } catch (IndexAlreadyExistsException e) {
                log.info("Index {} already exists, juste updating settings", index);
                
                // may have to update settings
                elasticsearchClient.admin().indices().close(new CloseIndexRequest(index)).actionGet();
                UpdateSettingsRequestBuilder usrb = new UpdateSettingsRequestBuilder(elasticsearchClient.admin().indices(), index);
                elasticsearchClient.admin().indices().updateSettings(usrb.setSettings(indexSettings).request()).actionGet();
                elasticsearchClient.admin().indices().open(new OpenIndexRequest(index)).actionGet();
                
            } catch (Exception e) {
                log.error("Failed to rebuild index {}", index, e);
            }
            
            // at the second level, we've types
            for (String type : indexTypes.get(index)) {
                try {
                    String typeMapping = ResourceUtils.getContentOfEmbeddedFile(MAPPING_DIR + "/" + index + "/" + type + ".json");
                    log.info("Updating mapping for {}/{}", index, type);
                    elasticsearchClient.admin().indices().preparePutMapping(index).setType(type).setSource(typeMapping).execute().actionGet();
                } catch (Exception e) {
                    log.error("Failed to read mapping or update mapping for ES", e);
                    log.error("THE MODE IS DETERIORED, YOU MAY HAVE TO MANUALY UPDATE MAPPING ! (see EdmAdministrationController)");
                }
            }

            flushIndex(index);
        }

        log.info("Building is over !");
    }

    public void updateMappingIfLocalNode() {
        log.info("Wan't update mapping...");
        if (elasticsearchClient != null && elasticsearchClient instanceof NodeClient) {
            buildOrUpdateEsMapping();
            return;
        }
        log.info("It's not a local node, I won't edit mapping");
    }

}
