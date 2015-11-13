package fr.simple.edm.crawler.bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmSource;

@Slf4j
public class EdmConnector {

    /**
     * Upload the given file and returns EDM token
     */
    public String uploadFile(String server, File file) throws ClientProtocolException {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", new FileBody(file));
        HttpEntity entity = builder.build();

        HttpPost request = new HttpPost(server + "/document/upload");
        request.setEntity(entity);

        String edmFileToken = "";

        HttpClient client = HttpClientBuilder.create().build();
        try {
            HttpResponse response = client.execute(request);

            HttpEntity getVarsEntity = response.getEntity();
            String pageString = "";

            if (getVarsEntity != null) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(getVarsEntity.getContent()));

               String line;
               while((line = reader.readLine()) != null){
                   pageString += line;
               }
            }

            log.debug("Upload response : {} ", pageString);

            Pattern p = Pattern.compile("\\{\"temporaryFileToken\":\"(.*)\"\\}");
            Matcher m = p.matcher(pageString);
            while(m.find()) {
                edmFileToken = m.group(1);
            }

            log.debug("Edm file token : {} ", edmFileToken);

        } catch (IOException e) {
            log.error("Failed to upload file", e);
        }
        return edmFileToken;
    }

    /**
     * Save the given document in EDMS
     */
    public void saveEdmDocument(String server, EdmDocumentFile doc) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(server + "/document", doc, EdmDocumentFile.class);
    }

    public void notifyStartCrawling(String server, String source) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet(server + "/crawl/start?source=" + sanitizeSourceName(source));
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
    }

    public void notifyEndOfCrawling(String server, String source) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet(server + "/crawl/stop?source=" + sanitizeSourceName(source));
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
    }

    private String sanitizeSourceName(String sourceName) {
        return sourceName.replaceAll(" ", "-");
    }

    public String getIdFromSourceBySourceName(String server, String sourceName, String categoryId) {
        // get Node
        RestTemplate restTemplate = new RestTemplate();
        EdmSource result = restTemplate.getForObject(server + "/source/name/{sourceName}", EdmSource.class, sourceName);

        // if exits, nothing to do !
        if (result.getId() != null && ! result.getId().isEmpty()) {
            return result.getId();
        }

        // else, we have to create it
        EdmSource directory = new EdmSource();
        directory.setDescription("");
        directory.setName(sourceName);
        directory.setParentId(categoryId);
        ResponseEntity<EdmSource> createdSource = restTemplate.postForEntity(server + "/source", directory, EdmSource.class);
        return createdSource.getBody().getId();
    }

    public String getIdFromCategoryByCategoryName(String server, String categoryName) {
        // get Node
        RestTemplate restTemplate = new RestTemplate();
        EdmCategory result = restTemplate.getForObject(server + "/category/name/{categoryName}", EdmCategory.class, categoryName);

        // if exits, nothing to do !
        if (result.getId() != null && ! result.getId().isEmpty()) {
            return result.getId();
        }

        // else, we have to create it
        EdmCategory category = new EdmCategory();
        category.setDescription("");
        category.setName(categoryName);
        ResponseEntity<EdmCategory> createdSource = restTemplate.postForEntity(server + "/category", category, EdmCategory.class);
        return createdSource.getBody().getId();
    }
}
