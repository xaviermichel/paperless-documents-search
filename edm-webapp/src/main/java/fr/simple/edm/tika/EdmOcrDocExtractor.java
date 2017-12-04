package fr.simple.edm.tika;

import fr.simple.edm.domain.EdmDocumentFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;

@Slf4j
@Component
public class EdmOcrDocExtractor {

    @Inject
    private TikaInstance tikaInstance;

    public void extractFileContent(EdmDocumentFile edmDocumentFile) {
        log.trace("Generating document [{}]", edmDocumentFile.getName());

        if (edmDocumentFile.getBinaryFileContent() == null) {
            log.trace("Binary content is null for [{}]", edmDocumentFile.getName());
            return;
        }

        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, edmDocumentFile.getNodePath());

            // fill content
            String fileContent = tikaInstance.extractFileContent(new ByteArrayInputStream(edmDocumentFile.getBinaryFileContent()), metadata);
            edmDocumentFile.setFileContent(fileContent);

            // fill metadata
            edmDocumentFile.setFileTitle(metadata.get(TikaCoreProperties.TITLE));
            edmDocumentFile.setFileKeywords(metadata.get(TikaCoreProperties.KEYWORDS));
            edmDocumentFile.setFileAuthor(metadata.get(TikaCoreProperties.CREATOR));

            log.debug("Tika extracted the following content : {}", fileContent);
        } catch (Throwable e) {
            log.debug("Failed to extract [{}] characters of text for [{}]", edmDocumentFile.getName(), e);
        }
        log.trace("End of generation for document [{}]", edmDocumentFile.getName());
    }
}

