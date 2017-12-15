package fr.simple.edm.crawler.bridge;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmSource;

public class EdmConnector {

    @Data
    @AllArgsConstructor
    public static class EdmCategoryColor {
        private String color;
        private String BackgroundColor;
    }

    private static List<EdmCategoryColor> edmCategoryColor;

    private static int currentEdmCategoryColorIndex = -1;

    // https://material.io/guidelines/style/color.html#color-color-palette
    static {
        edmCategoryColor = new ArrayList<>();
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#9E9E9E")); // Gray
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#3F51B5")); // Indigo
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#ff5722")); // Orange
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#4CAF50")); // Green
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#CDDC39")); // Lime
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#FFEB3B")); // Yellow
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#03a9f4")); // Light Blue
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#673AB7")); // Deep Purple
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#f44336")); // Red
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#2196F3")); // Blue
        edmCategoryColor.add(new EdmCategoryColor("#FFF", "#795548")); // Brown
    }

    public void saveEdmDocument(String server, EdmDocumentFile doc, File file) throws IOException {
        doc.setBinaryFileContent(Files.readAllBytes(file.toPath()));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(server + "/crawl/document", doc, EdmDocumentFile.class);
    }

    public void notifyStartCrawling(String server, String source) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet(server + "/crawl/start?source=" + URLEncoder.encode(source, "UTF-8"));
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
    }

    public void notifyEndOfCrawling(String server, String source) throws ClientProtocolException, IOException {
        HttpGet request = new HttpGet(server + "/crawl/stop?source=" + URLEncoder.encode(source, "UTF-8"));
        HttpClient client = HttpClientBuilder.create().build();
        client.execute(request);
    }

    public String getIdFromSourceBySourceName(String server, String sourceName, String categoryId) {
        // get Node
        RestTemplate restTemplate = new RestTemplate();
        EdmSource result = restTemplate.getForObject(server + "/source/name/{sourceName}", EdmSource.class, sourceName);

        // if exits, nothing to do !
        if (result.getId() != null && !result.getId().isEmpty()) {
            return result.getId();
        }

        // else, we have to create it
        EdmSource directory = new EdmSource();
        directory.setDescription("");
        directory.setName(sourceName);
        ResponseEntity<EdmSource> createdSource = restTemplate.postForEntity(server + "/source", directory, EdmSource.class);
        return createdSource.getBody().getId();
    }

    public String getIdFromCategoryByCategoryName(String server, String categoryName) {
        // get Node
        RestTemplate restTemplate = new RestTemplate();
        EdmCategory result = restTemplate.getForObject(server + "/category/name/{categoryName}", EdmCategory.class, categoryName);

        // if exits, nothing to do !
        if (result.getId() != null && !result.getId().isEmpty()) {
            return result.getId();
        }

        // else, we have to create it
        currentEdmCategoryColorIndex++;
        EdmCategory category = new EdmCategory();
        category.setDescription("");
        category.setName(categoryName);
        category.setColor(edmCategoryColor.get(currentEdmCategoryColorIndex % edmCategoryColor.size()).getColor());
        category.setBackgroundColor(edmCategoryColor.get(currentEdmCategoryColorIndex % edmCategoryColor.size()).getBackgroundColor());
        ResponseEntity<EdmCategory> createdSource = restTemplate.postForEntity(server + "/category", category, EdmCategory.class);
        return createdSource.getBody().getId();
    }
}
