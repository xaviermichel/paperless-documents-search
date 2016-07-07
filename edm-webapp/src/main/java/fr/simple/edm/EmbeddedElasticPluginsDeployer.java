package fr.simple.edm;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import fr.simple.edm.util.ResourceUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Since elastic 2.2, es plugins in classpath aren't detected and loaded.
 *
 * The easiest way to use plugin on local node (for development), is to manually
 * deploy plugins in elastic plugins directory.
 *
 * @author xavier
 */
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class EmbeddedElasticPluginsDeployer {

	private static final String ES_EMBEDDED_PLUGINS_DIR = "/elastic_settings/plugins/";

	@Value("${spring.data.elasticsearch.clusterNodes:#{null}}")
	private String clusterNodes;

	@Value("${spring.data.elasticsearch.properties.path.home:#{null}}")
	private String elasticHomeDirectory;

	@Data
	class EmbeddedElasticPlugins {
		private String zipName;
		private String dirTargetName;
		private List<String> excludedJars = new ArrayList<>();
	}

	private List<EmbeddedElasticPlugins> pluginsToDeploy = new ArrayList<>();

	public EmbeddedElasticPluginsDeployer() {
		EmbeddedElasticPlugins mapperAttachment = new EmbeddedElasticPlugins();
		mapperAttachment.setZipName("mapper-attachments-2.3.3.zip");
		mapperAttachment.setDirTargetName("mapper-attachments");
		mapperAttachment.setExcludedJars(Lists.newArrayList("commons-codec-1.10.jar", "commons-io-2.4.jar", "commons-logging-1.1.3.jar"));

		pluginsToDeploy.add(mapperAttachment);
	}
	
	@PostConstruct
	public void init() {
		if (StringUtils.hasLength(clusterNodes)) {
			log.info("Not a local node, won't deploy additionals plugins (on {})", clusterNodes);
			return;
		}

		Path pluginsDirectory = Paths.get(".", elasticHomeDirectory, "plugins");

		if (Files.isDirectory(pluginsDirectory)) {
			log.info("pluginsDirectory already exists, won't re-deploy !");
			return;
		}

		log.info("Deploying elastic plugins into {}", elasticHomeDirectory);
		
		if (! StringUtils.hasLength(elasticHomeDirectory)) {
			log.error("elasticHomeDirectory is not defined, I don't known where I can deploy elastic plugins !");
			return;
		}
		
		try {
			Files.createDirectories(pluginsDirectory);
		} catch (IOException e) {
			log.error("Failed to create '{}'", pluginsDirectory, e);
		}

		for (EmbeddedElasticPlugins pluginToDeploy : pluginsToDeploy) {

			log.debug("Deploying {}", pluginToDeploy.getZipName());

			// classpath:/plugins/a.zip => es/plugins/a.zip
			URL inputUrl = getClass().getResource(ES_EMBEDDED_PLUGINS_DIR + pluginToDeploy.getZipName());
			Path outputZipFile = Paths.get(".", elasticHomeDirectory, "plugins", pluginToDeploy.getZipName());
			try {
				log.debug("Copying '{}' to '{}'", inputUrl, outputZipFile);
				FileUtils.copyURLToFile(inputUrl, outputZipFile.toFile());
			} catch (IOException e) {
				log.error("Failed to extract '{}' from classpath", pluginsDirectory, e);
			}

			// es/plugins/a.zip => es/plugins/a
			try {
				Path outputZipDirectory = Paths.get(".", elasticHomeDirectory, "plugins", pluginToDeploy.getDirTargetName());
				log.debug("Extracting zip '{}' to '{}'", outputZipFile, outputZipDirectory);
				ResourceUtils.extractZip(outputZipFile.toFile(), outputZipDirectory.toFile());
			} catch (IOException e) {
				log.error("Failed to extract '{}' zip content", pluginsDirectory, e);
			}

			// remove unwanted dependencies
			for (String pluginFileToRemove : pluginToDeploy.getExcludedJars()) {
				Path fileToRemove = Paths.get(".", elasticHomeDirectory, "plugins", pluginToDeploy.getDirTargetName(), pluginFileToRemove);
				log.debug("Rempoving excluded jar : '{}'", fileToRemove);
				try {
					Files.delete(fileToRemove);
				} catch (IOException e) {
					log.error("Failed to delete unwanted dependency (may you will have jarHell !) '{}'", fileToRemove, e);
				}
			}

			// remove temporary zip (es/plugins/a.zip)
			try {
				log.debug("Removing temporary zip : '{}'", outputZipFile);
				Files.delete(outputZipFile);
			} catch (IOException e) {
				log.error("Failed to delete temporary zip '{}'", outputZipFile, e);
			}
		}

		log.info("End of elastic plugins deployment !");
	}
}
