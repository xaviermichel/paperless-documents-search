package fr.simple.edm.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.simple.edm.domain.EdmCategoryDto;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmSourceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class EdmConnector {

	private static List<EdmCategoryColor> edmCategoryColor;

	private static int currentEdmCategoryColorIndex = -1;

	private static RestTemplate restTemplate;

	// https://material.io/guidelines/style/color.html#color-color-palette
	static {
		edmCategoryColor = new ArrayList<>();
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#9E9E9E")); // Gray
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#3F51B5")); // Indigo
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#ff5722")); // Orange
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#4CAF50")); // Green
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#CDDC39")); // Lime
		edmCategoryColor.add(new EdmCategoryColor("#000", "#FFEB3B")); // Yellow
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#03a9f4")); // Light Blue
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#673AB7")); // Deep Purple
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#f44336")); // Red
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#2196F3")); // Blue
		edmCategoryColor.add(new EdmCategoryColor("#FFF", "#795548")); // Brown

		RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
		restTemplate = restTemplateBuilder.build();
	}

	public static String sanitizeSourceName(String sourceName) {
		return sourceName.replaceAll(" ", "_").replaceAll("/", "_");
	}

	public void saveEdmDocument(String server, EdmDocumentFileDto doc, File file) throws IOException {
		doc.setBinaryFileContent(Files.readAllBytes(file.toPath()));

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(server + "/api/crawling/document", doc, EdmDocumentFileDto.class);
	}

	public void notifyStartCrawling(String server, String source) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(server + "/api/crawling/start?source=" + URLEncoder.encode(source, "UTF-8"));
		HttpClient client = HttpClientBuilder.create().build();
		client.execute(request);
	}

	public void notifyEndOfCrawling(String server, String source) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(server + "/api/crawling/finish?source=" + URLEncoder.encode(source, "UTF-8"));
		HttpClient client = HttpClientBuilder.create().build();
		client.execute(request);
	}

	public String getIdFromSourceBySourceName(String server, String sourceName, String categoryId) {
		// get Node
		EdmSourceDto result = restTemplate.getForObject(server + "/api/sources?sourceName={sourceName}", EdmSourceDto.class, sourceName);

		// if exits, nothing to do !
		if (result.getId() != null && !result.getId().isEmpty()) {
			return result.getId();
		}

		// else, we have to create it
		EdmSourceDto directory = new EdmSourceDto();
		directory.setDescription("");
		directory.setName(sourceName);
		ResponseEntity<EdmSourceDto> createdSource = restTemplate.postForEntity(server + "/api/sources", directory, EdmSourceDto.class);
		return createdSource.getBody().getId();
	}

	public String getIdFromCategoryByCategoryName(String server, String categoryName) {
		// get Node
		EdmCategoryDto result = restTemplate.getForObject(server + "/api/categories?categoryName={categoryName}", EdmCategoryDto.class, categoryName);

		// if exits, nothing to do !
		if (result.getId() != null && !result.getId().isEmpty()) {
			return result.getId();
		}

		// else, we have to create it
		currentEdmCategoryColorIndex++;
		EdmCategoryDto category = new EdmCategoryDto();
		category.setDescription("");
		category.setName(categoryName);
		category.setColor(edmCategoryColor.get(currentEdmCategoryColorIndex % edmCategoryColor.size()).getColor());
		category.setBackgroundColor(edmCategoryColor.get(currentEdmCategoryColorIndex % edmCategoryColor.size()).getBackgroundColor());
		ResponseEntity<EdmCategoryDto> createdSource = restTemplate.postForEntity(server + "/api/categories", category, EdmCategoryDto.class);
		return createdSource.getBody().getId();
	}

	@Data
	@AllArgsConstructor
	public static class EdmCategoryColor {
		private String color;

		private String BackgroundColor;
	}
}
