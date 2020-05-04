package fr.simple.edm.clients;

import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmSourceDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "edm-document-repository-app", url = "${edm-document-repository-app.uri:'http://edm-document-repository-app'}")
public interface DocumentRepositoryClient {

	@RequestMapping(method = RequestMethod.GET, value = "/source/name/{sourceName}")
	EdmSourceDto getSourceByName(@PathVariable("sourceName") String sourceName);

	@RequestMapping(method = RequestMethod.GET, value = "/sources/start-crawling", params = { "source" })
	void startCrawling(@RequestParam(value = "source") String source);

	@RequestMapping(method = RequestMethod.GET, value = "/sources/finish-crawling", params = { "source" })
	void stopCrawling(@RequestParam(value = "source") String source);

	@RequestMapping(method = RequestMethod.POST, value = "/documents")
	EdmDocumentFileDto save(@RequestBody EdmDocumentFileDto edmDocument);

}
