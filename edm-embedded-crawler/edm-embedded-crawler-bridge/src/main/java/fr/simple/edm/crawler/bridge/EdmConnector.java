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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import fr.simple.edm.common.dto.EdmDocumentFileDto;

public class EdmConnector {

    private Logger logger = LoggerFactory.getLogger(EdmConnector.class);
    
    /**
     * Upload the given file and returns EDM token
     */
    public String uploadFile(String server, File file) throws ClientProtocolException {
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("file", new FileBody(file));

        HttpPost request = new HttpPost("http://" + server + "/document/upload");
        request.setEntity(entity);

        String edmFileToken = "";
        
        HttpClient client = new DefaultHttpClient();
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
        
            logger.info("Edm file token : {} ", edmFileToken);
            
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
}
