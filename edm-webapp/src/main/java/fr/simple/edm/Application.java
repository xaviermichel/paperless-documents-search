package fr.simple.edm;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application {

    @Inject
    private ElasticsearchConfig elasticsearchConfig;
    
    @Value("${info.app.name:''}")
    private String applicationName;
    
    @Value("${info.app.version:''}")
    private String applicationVersion;
    
    @Value("${info.app.issues_url:''}")
    private String applicationIssueUrl;
    
    @Value("${edm.tmpdir}")
    private String edmTmpsdir;

    @Value("${server.address:127.0.0.1}")
    private String serverAddress;
    
    @Value("${server.port}")
    private Integer serverPort;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
    
    @PostConstruct
    public void init() {
        log.info("==================================================================================");
        log.info("Hi, this is {} version {}", applicationName, applicationVersion);
        log.info("You can report issues on {}", applicationIssueUrl);
        log.info("----------------------------------------------------------------------------------");
        log.info("java.runtime.name          : " + System.getProperty("java.runtime.name"));
        log.info("java.runtime.version       : " + System.getProperty("java.runtime.version"));
        log.info("java.specification.name    : " + System.getProperty("java.specification.name"));
        log.info("java.specification.vendor  : " + System.getProperty("java.specification.vendor"));
        log.info("java.specification.version : " + System.getProperty("java.specification.version"));
        log.info("java.vendor                : " + System.getProperty("java.vendor"));
        log.info("java.version               : " + System.getProperty("java.version"));
        log.info("java.vm.info               : " + System.getProperty("java.vm.info"));
        log.info("java.vm.name               : " + System.getProperty("java.vm.name"));
        log.info("java.vm.version            : " + System.getProperty("java.vm.version"));
        log.info("os.arch                    : " + System.getProperty("os.arch"));
        log.info("os.name                    : " + System.getProperty("os.name"));
        log.info("os.version                 : " + System.getProperty("os.version"));
        log.info("==================================================================================");

        try {
			elasticsearchConfig.updateMappingIfLocalNode();
		} catch (IOException e) {
			log.error("something failed while configuring elastic", e);
		}
        
        // create temporary directory
        if (! new File(edmTmpsdir).mkdirs()) {
            log.warn("failed to create temporary directory ({}), may already exists ?", edmTmpsdir);
        }
        
        log.info("server is starting and listening on {}:{}", serverAddress, serverPort);
    }
}
