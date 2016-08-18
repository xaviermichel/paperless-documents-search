package fr.simple.edm;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

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
        log.info("==========================================================================================");
        log.info("Hi, this is {} version {}", applicationName, applicationVersion);
        log.info("You can report issues on {}", applicationIssueUrl);
        log.info("==========================================================================================");

        try {
            elasticsearchConfig.updateMappingIfLocalNode();
        } catch (IOException e) {
            log.error("something failed while configuring elastic", e);
        }

        log.info("server is starting and listening on {}:{}", serverAddress, serverPort);
    }
}
