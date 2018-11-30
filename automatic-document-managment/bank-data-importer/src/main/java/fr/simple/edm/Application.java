package fr.simple.edm;

import fr.simple.edm.domain.AccountOperation;
import fr.simple.edm.service.CABankDataTranslator;
import fr.simple.edm.service.GoogleSheetTranslator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Slf4j
@Component
@EnableConfigurationProperties({ GoogleSheetImporterConfiguration.class, CAConfiguration.class })
public class Application implements CommandLineRunner {

    @Autowired
    private GoogleSheetTranslator googleSheetTranslator;

    @Autowired
    private CAConfiguration caConfiguration;

    @Autowired
    private CABankDataTranslator caBankDataTranslator;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void run(String... args) throws Exception {

        List<AccountOperation> operations = new ArrayList<>();

        if (!StringUtils.isBlank(caConfiguration.getCsvFilePath())) {
            log.info("Going to extract values for bank CA, file {}", caConfiguration.getCsvFilePath());
            operations.addAll(caBankDataTranslator.fileToAccountOperations(caConfiguration.getCsvFilePath()));
        }

        log.info("Operations : {}", operations);

        googleSheetTranslator.clear();
        googleSheetTranslator.reloadData(operations);
    }

}
