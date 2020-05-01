package fr.simple.edm.controller;

import fr.simple.edm.clients.DocumentRepositoryClient;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.service.EdmDocumentService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawling")
@Slf4j
public class EdmCrawlingController {

	private final EdmDocumentService edmDocumentService;

	private final DocumentRepositoryClient documentRepositoryClient;

	public EdmCrawlingController(EdmDocumentService edmDocumentService, DocumentRepositoryClient documentRepositoryClient) {
		this.edmDocumentService = edmDocumentService;
		this.documentRepositoryClient = documentRepositoryClient;
	}

	@RequestMapping(value = "/start", params = { "source" })
	@ResponseStatus(value = HttpStatus.OK)
	public void startCrawling(@RequestParam(value = "source") String source) {
		log.info("Begin crawling for source : {}", source);
		documentRepositoryClient.startCrawling(source);
	}

	@RequestMapping(value = "/finish", params = { "source" })
	@ResponseStatus(value = HttpStatus.OK)
	public void stopCrawling(@RequestParam(value = "source") String source) {
		log.info("End of crawling for source : {}", source);
		documentRepositoryClient.stopCrawling(source);
	}

	@RequestMapping(value = "/document", method = RequestMethod.POST)
	public EdmDocumentFileDto create(@RequestBody EdmDocumentFileDto edmDocument) {
		return edmDocumentService.save(edmDocument);
	}

}
