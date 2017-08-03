package fr.simple.edm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Slf4j
public class Application {


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
        log.info("server is starting and listening on {}:{}", serverAddress, serverPort);
    }
}
