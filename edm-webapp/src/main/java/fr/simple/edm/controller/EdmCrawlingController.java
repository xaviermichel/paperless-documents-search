package fr.simple.edm.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;
import fr.simple.edm.service.EdmDocumentService;

@Controller
public class EdmCrawlingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdmCrawlingController.class);

    @Inject
    private EdmDocumentService edmDocumentService;

    @RequestMapping(value = "/crawl/start", method = RequestMethod.GET, params = {"source"})
    @ResponseStatus(value=HttpStatus.OK)
    public void startCrawling(@RequestParam(value = "source") String source) {
        LOGGER.info("Begin crawling for source : {}", source);
        edmDocumentService.snapshotCurrentDocumentsForSource(source);
    }

    @RequestMapping(value = "/crawl/stop", method = RequestMethod.GET, params = {"source"})
    @ResponseStatus(value=HttpStatus.OK)
    public void stopCrawling(@RequestParam(value = "source") String source) {
        LOGGER.info("End of crawling for source : {}", source);
        edmDocumentService.deleteUnusedDocumentsBeforeSnapshotForSource(source);
    }

    @RequestMapping(value = "/crawl/filesystem", method = RequestMethod.GET, params = {"path"})
    @ResponseBody
    public String crawlFilesystem(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "edmServerHttpAddress", defaultValue = "127.0.0.1:8053") String edmServerHttpAddress,
            @RequestParam(value = "sourceName", defaultValue = "unmanned source") String sourceName,
            @RequestParam(value = "categoryName", defaultValue = "unmanned category") String categoryName,
            @RequestParam(value = "exclusionRegex", defaultValue = "") String exclusionRegex
       ) {
        LOGGER.info("Starting crawling on path : '{}'  (exclusion = '{}')", path, exclusionRegex);
        try {
            FilesystemCrawler.importFilesInDir(path, edmServerHttpAddress, sourceName, categoryName, exclusionRegex);
        } catch (Exception e) {
            LOGGER.error("Failed to crawl '{}' with embedded crawler", path, e);
        }

        return "OK";
    }
}
