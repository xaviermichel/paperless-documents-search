package fr.simple.edm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DocumentIngestApplication {
	public static void main(String[] args) {
		SpringApplication.run(DocumentIngestApplication.class, args);
	}
}
