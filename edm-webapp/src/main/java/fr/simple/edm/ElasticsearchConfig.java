package fr.simple.edm;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.Charsets;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;

@EnableElasticsearchRepositories(basePackages = "fr.simple.edm.repository")
@Service
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

    /**
     * @warning returns null if cannot get content
     */
    private String getContentOfEmbeddedFile(String embeddedPath) {
        try {
            URL url = Resources.getResource(embeddedPath);
            String content = Resources.toString(url, Charsets.UTF_8);
            return content;
        }
        catch (Exception e) {
            log.warn("Failed to get content of file " + embeddedPath, e);
        }
        return null;
    }
    
    private void buildOrUpdateEsMapping() {

        log.info("Updating ES mapping because you're using a local node");

        Map<String, List<String>> indexTypes = new HashMap<>();
        indexTypes.put("documents", Lists.newArrayList("category", "source", "document_file"));

        for (String index : indexTypes.keySet()) {

            // create index, with settings if exists
            String indexSettings = getContentOfEmbeddedFile(MAPPING_DIR + "/" + index + ".json");
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
                    String typeMapping = getContentOfEmbeddedFile(MAPPING_DIR + "/" + index + "/" + type + ".json");
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
