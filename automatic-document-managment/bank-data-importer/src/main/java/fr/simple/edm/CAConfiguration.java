package fr.simple.edm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "ca")
@Data
@Primary
public class CAConfiguration {

    private String csvFilePath;

    private List<String> accountsLabel;

}
