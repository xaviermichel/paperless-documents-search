package fr.simple.edm.tika;

import java.io.ByteArrayInputStream;

import fr.simple.edm.domain.EdmDocumentFileDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EdmOcrDocExtractor {

	private final TikaInstance tikaInstance;

	public EdmOcrDocExtractor(TikaInstance tikaInstance) {
		this.tikaInstance = tikaInstance;
	}

	public void extractFileContent(EdmDocumentFileDto edmDocumentFileDto) {
		log.trace("Generating document [{}]", edmDocumentFileDto.getName());

		if (edmDocumentFileDto.getBinaryFileContent() == null) {
			log.trace("Binary content is null for [{}]", edmDocumentFileDto.getName());
			return;
		}

		try {
			Metadata metadata = new Metadata();
			metadata.set(Metadata.RESOURCE_NAME_KEY, edmDocumentFileDto.getNodePath());

			// fill content
			String fileContent = tikaInstance.extractFileContent(new ByteArrayInputStream(edmDocumentFileDto.getBinaryFileContent()), metadata);
			edmDocumentFileDto.setFileContent(fileContent.trim());

			// fill metadata
			edmDocumentFileDto.setFileTitle(metadata.get(TikaCoreProperties.TITLE));
			edmDocumentFileDto.setFileKeywords(metadata.get(TikaCoreProperties.KEYWORDS));
			edmDocumentFileDto.setFileAuthor(metadata.get(TikaCoreProperties.CREATOR));

			log.debug("Tika extracted the following content : {}", fileContent);
		}
		catch (Throwable e) {
			log.debug("Failed to extract [{}] characters of text for [{}]", edmDocumentFileDto.getName(), e);
		}
		log.trace("End of generation for document [{}]", edmDocumentFileDto.getName());
	}
}

