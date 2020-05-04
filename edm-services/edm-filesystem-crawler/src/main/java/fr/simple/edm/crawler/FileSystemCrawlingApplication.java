package fr.simple.edm.crawler;

import java.io.File;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;
import fr.simple.edm.utils.EdmConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Slf4j
@Component
@EnableConfigurationProperties({ CrawlingConfiguration.class })
public class FileSystemCrawlingApplication implements CommandLineRunner {

	@Autowired
	private CrawlingConfiguration crawlingConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(FileSystemCrawlingApplication.class, args);
	}

	public void run(String... args) throws Exception {

		// crawl root files
		String rootCategoryName = FilenameUtils.getBaseName(crawlingConfiguration.getRootPath());
		FilesystemCrawler.importFilesInDir(
				crawlingConfiguration.getRootPath(),
				crawlingConfiguration.getEdmServerHttpAddress(),
				EdmConnector.sanitizeSourceName(crawlingConfiguration.getSourceName()),
				rootCategoryName,
				crawlingConfiguration.getExclusionRegex(),
				false);

		// crawl each subdirectory
		String[] directories = new File(crawlingConfiguration.getRootPath()).list((current, name) -> new File(current, name).isDirectory());
		for (String directory : directories) {
			log.info("crawling directory {}", directory);
			String categoryName = FilenameUtils.getBaseName(directory);
			FilesystemCrawler.importFilesInDir(
					crawlingConfiguration.getRootPath() + "/" + directory,
					crawlingConfiguration.getEdmServerHttpAddress(),
					EdmConnector.sanitizeSourceName(crawlingConfiguration.getRootPath() + "/" + directory),
					categoryName,
					crawlingConfiguration.getExclusionRegex(),
					true
			);
		}
	}

}
