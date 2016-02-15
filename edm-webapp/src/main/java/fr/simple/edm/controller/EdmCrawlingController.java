package fr.simple.edm.controller;

import java.io.IOException;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;
import fr.simple.edm.crawler.url.UrlCrawler;
import fr.simple.edm.service.EdmDocumentService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class EdmCrawlingController {

    @Inject
    private EdmDocumentService edmDocumentService;

    @RequestMapping(value = "/crawl/start", method = RequestMethod.GET, params = {"source"})
    @ResponseStatus(value=HttpStatus.OK)
    public void startCrawling(@RequestParam(value = "source") String source) {
        log.info("Begin crawling for source : {}", source);
        edmDocumentService.snapshotCurrentDocumentsForSource(source);
    }

    @RequestMapping(value = "/crawl/stop", method = RequestMethod.GET, params = {"source"})
    @ResponseStatus(value=HttpStatus.OK)
    public void stopCrawling(@RequestParam(value = "source") String source) {
        log.info("End of crawling for source : {}", source);
        edmDocumentService.deleteUnusedDocumentsBeforeSnapshotForSource(source);
    }

    @RequestMapping(value = "/crawl/filesystem", method = RequestMethod.GET, params = {"path"})
    @ResponseBody
    public String crawlFilesystem(
            @RequestParam(value = "path") String path,
            @RequestParam(value = "edmServerHttpAddress", defaultValue = "http://127.0.0.1:8053") String edmServerHttpAddress,
            @RequestParam(value = "sourceName", defaultValue = "unmanned source") String sourceName,
            @RequestParam(value = "categoryName", defaultValue = "unmanned category") String categoryName,
            @RequestParam(value = "exclusionRegex", defaultValue = "") String exclusionRegex
       ) {
        log.info("[crawlFilesystem] Starting crawling on path : '{}'  (exclusion = '{}')", path, exclusionRegex);
        try {
            FilesystemCrawler.importFilesInDir(path, edmServerHttpAddress, sourceName, categoryName, exclusionRegex);
        } catch (IOException e) {
            log.error("[crawlFilesystem] Failed to crawl '{}' with embedded crawler", path, e);
        }

        return "OK";
    }


    @RequestMapping(value = "/crawl/url", method = RequestMethod.GET, params = {"url"})
    @ResponseBody
    public String crawlUrl(
            @RequestParam(value = "url") String url,
            @RequestParam(value = "edmServerHttpAddress", defaultValue = "http://127.0.0.1:8053") String edmServerHttpAddress,
            @RequestParam(value = "sourceName", defaultValue = "unmanned source") String sourceName,
            @RequestParam(value = "categoryName", defaultValue = "unmanned category") String categoryName,
            @RequestParam(value = "exclusionRegex", defaultValue = "") String exclusionRegex
       ) {
        log.info("[crawlUrl] Starting crawling on path : '{}'  (exclusion = '{}')", url, exclusionRegex);
        try {
            UrlCrawler.importFilesAtUrl(url, edmServerHttpAddress, sourceName, categoryName, exclusionRegex);
        } catch (IOException e) {
            log.error("[crawlUrl] Failed to crawl '{}' with embedded crawler", url, e);
        }

        return "OK";
    }
}
