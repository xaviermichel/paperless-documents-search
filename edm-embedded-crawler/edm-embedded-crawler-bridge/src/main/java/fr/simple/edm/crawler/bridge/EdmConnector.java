package fr.simple.edm.crawler.bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.simple.edm.common.dto.EdmDocumentFileDto;
import fr.simple.edm.common.dto.EdmSourceDto;

public class EdmConnector {

    private Logger logger = LoggerFactory.getLogger(EdmConnector.class);
    
    /**
     * Upload the given file and returns EDM token
     */
    public String uploadFile(String server, File file) throws ClientProtocolException {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);        
        builder.addPart("file", new FileBody(file)); 
        HttpEntity entity = builder.build();
        
        HttpPost request = new HttpPost("http://" + server + "/document/upload");
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
            
            logger.debug("Upload response : {} ", pageString);
        
            Pattern p = Pattern.compile("\\{\"temporaryFileToken\":\"(.*)\"\\}");
            Matcher m = p.matcher(pageString);
            while(m.find()) {
                edmFileToken = m.group(1);
            }
        
            logger.debug("Edm file token : {} ", edmFileToken);
            
        } catch (IOException e) {
            logger.error("Failed to upload file", e);
        }
        return edmFileToken;
    }
    
    /**
     * Save the given document in EDMS
     */
    public void saveEdmDocument(String server, EdmDocumentFileDto doc) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://" + server + "/document", doc, EdmDocumentFileDto.class);
    }
    
    public void notifyStartCrawling(String server, String source) throws ClientProtocolException, IOException {
    	HttpGet request = new HttpGet("http://" + server + "/crawl/start?source=" + sanitizeSourceName(source));
    	HttpClient client = HttpClientBuilder.create().build();
		client.execute(request);
    }
    
    public void notifyEndOfCrawling(String server, String source) throws ClientProtocolException, IOException {
    	HttpGet request = new HttpGet("http://" + server + "/crawl/stop?source=" + sanitizeSourceName(source));
    	HttpClient client = HttpClientBuilder.create().build();
		client.execute(request);
    }
    
    private String sanitizeSourceName(String sourceName) {
    	return sourceName.replaceAll(" ", "-");
    }
    
    public String getIdFromSourceBySourceName(String server, String sourceName) {
    	// get Node 
        RestTemplate restTemplate = new RestTemplate();
        EdmSourceDto result = restTemplate.getForObject("http://" + server + "/source/name/{sourceName}", EdmSourceDto.class, sourceName);
        
        // if exits, nothing to do !
        if (result.getId() != null && ! result.getId().isEmpty()) {
            return result.getId();
        }
        
        // else, we have to create it
        EdmSourceDto directory = new EdmSourceDto();
        directory.setDescription("");
        directory.setName(sourceName);
        //directory.setParentId(parentId);
        ResponseEntity<EdmSourceDto> createdSource = restTemplate.postForEntity("http://" + server + "/directory", directory, EdmSourceDto.class);
        return createdSource.getBody().getId();
    }
}
