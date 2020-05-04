package fr.simple.edm.crawler;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "edm.crawler.fs")
@Data
public class CrawlingConfiguration {

	private String rootPath = "/media";

	private String edmServerHttpAddress = "http://127.0.0.1:80";

	private String sourceName = "local_medias_source";

	private String exclusionRegex = "";

}
