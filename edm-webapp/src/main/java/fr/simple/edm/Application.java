package fr.simple.edm;

import java.io.File;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@SpringBootApplication
@PropertySources(value = {
        @PropertySource("classpath:/properties/constants.properties"),
        @PropertySource("classpath:/edm-configuration.properties"),
        @PropertySource("classpath:/application.properties")
    }
)
public class Application {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static Environment env;
    
    @Inject
    public void setEnv(Environment env) {
        Application.env = env;
    }
    
    private static ElasticsearchConfig elasticsearchConfig;
    
    @Inject
    public void setElasticsearchConfig(ElasticsearchConfig elasticsearchConfig) {
        Application.elasticsearchConfig = elasticsearchConfig;
    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);
        app.run(args);
        
        // Run this logs AFTER spring bean injection !
        LOGGER.info("==================================================================================");
        LOGGER.info("Hi, this is {} version {}", env.getProperty("APPLICATION_NAME"), env.getProperty("APPLICATION_VERSION"));
        LOGGER.info("You can report issues on {}", env.getProperty("APPLICATION_ISSUES_URL"));
        LOGGER.info("----------------------------------------------------------------------------------");
        LOGGER.info("java.runtime.name          : " + System.getProperty("java.runtime.name"));
        LOGGER.info("java.runtime.version       : " + System.getProperty("java.runtime.version"));
        LOGGER.info("java.specification.name    : " + System.getProperty("java.specification.name"));
        LOGGER.info("java.specification.vendor  : " + System.getProperty("java.specification.vendor"));
        LOGGER.info("java.specification.version : " + System.getProperty("java.specification.version"));
        LOGGER.info("java.vendor                : " + System.getProperty("java.vendor"));
        LOGGER.info("java.version               : " + System.getProperty("java.version"));
        LOGGER.info("java.vm.info               : " + System.getProperty("java.vm.info"));
        LOGGER.info("java.vm.name               : " + System.getProperty("java.vm.name"));
        LOGGER.info("java.vm.version            : " + System.getProperty("java.vm.version"));
        LOGGER.info("os.arch                    : " + System.getProperty("os.arch"));
        LOGGER.info("os.name                    : " + System.getProperty("os.name"));
        LOGGER.info("os.version                 : " + System.getProperty("os.version"));
        LOGGER.info("==================================================================================");

        elasticsearchConfig.updateMappingIfLocalNode();
        
        // create temporary directory
        if (! new File(env.getProperty("edm.tmpdir")).mkdirs()) {
            LOGGER.warn("Failed to create temporary directory ({}), may already exists ?", env.getProperty("edm.tmpdir"));
        }
        
        LOGGER.info("Startup is finished ! Waiting for some user on http://127.0.0.1:{}", env.getProperty("server.port"));
    }
}
