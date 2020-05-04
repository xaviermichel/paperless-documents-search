package fr.simple.edm.service;

import fr.simple.edm.clients.DocumentRepositoryClient;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.tika.EdmOcrDocExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EdmDocumentService {

	private final EdmOcrDocExtractor edmOcrDocExtractor;

	private final DocumentRepositoryClient documentRepositoryClient;

	public EdmDocumentService(EdmOcrDocExtractor edmOcrDocExtractor, DocumentRepositoryClient documentRepositoryClient) {
		this.edmOcrDocExtractor = edmOcrDocExtractor;
		this.documentRepositoryClient = documentRepositoryClient;
	}

	public EdmDocumentFileDto save(EdmDocumentFileDto edmDocument) {

		// unique identifier for updating
		String id = DigestUtils.md5Hex(edmDocument.getNodePath() + "@" + edmDocument.getSourceId());
		edmDocument.setId(id);

		// read the file content
		edmOcrDocExtractor.extractFileContent(edmDocument);

		// force not index of binary content
		edmDocument.setBinaryFileContent(null);

		edmDocument = documentRepositoryClient.save(edmDocument);
		return edmDocument;
	}
}
