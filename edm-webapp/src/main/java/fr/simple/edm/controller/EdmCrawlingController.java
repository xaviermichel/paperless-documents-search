package fr.simple.edm.controller;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;
import fr.simple.edm.crawler.url.UrlCrawler;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.service.EdmCrawlingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/crawl")
@Slf4j
public class EdmCrawlingController {

    @Inject
    private EdmCrawlingService edmCrawlingService;

    @RequestMapping(value = "/start", params = {"source"})
    @ResponseStatus(value = HttpStatus.OK)
    public void startCrawling(@RequestParam(value = "source") String source) {
        log.info("Begin crawling for source : {}", source);
        edmCrawlingService.snapshotCurrentDocumentsForSource(source);
    }

    @RequestMapping(value = "/stop", params = {"source"})
    @ResponseStatus(value = HttpStatus.OK)
    public void stopCrawling(@RequestParam(value = "source") String source) {
        log.info("End of crawling for source : {}", source);
        edmCrawlingService.deleteUnusedDocumentsBeforeSnapshotForSource(source);
    }

    @RequestMapping(value = "/filesystem", params = {"path"})
    @ResponseBody
    public String crawlFilesystem(
        @RequestParam(value = "path") String path,
        @RequestParam(value = "edmServerHttpAddress", defaultValue = "http://localhost:8053") String edmServerHttpAddress,
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

    /*
     * Crawl by assuming that each subdir is a different category, assuming  1 directory=1 source
     * @warning This will auto-create categories !
     */
    @RequestMapping(value = "/filesystem/subdirectories", params = {"path"})
    @ResponseBody
    public String crawlFilesystemSubdirectories(
        @RequestParam(value = "path") String path,
        @RequestParam(value = "edmServerHttpAddress", defaultValue = "http://localhost:8053") String edmServerHttpAddress,
        @RequestParam(value = "exclusionRegex", defaultValue = "") String exclusionRegex
    ) {
        log.info("[crawlFilesystem] Starting crawling on path : '{}'  (exclusion = '{}')", path, exclusionRegex);
        try {
            // crawl root files
            String rootSanitizedSourceName = path.replaceAll(" ", "_").replaceAll("/", "_");
            String rootCategoryName = FilenameUtils.getBaseName(path);
            FilesystemCrawler.importFilesInDir(path, edmServerHttpAddress, rootSanitizedSourceName, rootCategoryName, exclusionRegex, false);

            // crawl each subdirectory
            String[] directories = new File(path).list((current, name) -> new File(current, name).isDirectory());
            for (String directory : directories) {
                log.debug("crawling directory {}", directory);
                String sanitizedSourceName = (path + "/" + directory).replaceAll(" ", "_").replaceAll("/", "_");
                String categoryName = FilenameUtils.getBaseName(directory);
                FilesystemCrawler.importFilesInDir(path + "/" + directory, edmServerHttpAddress, sanitizedSourceName, categoryName, exclusionRegex);
            }

        } catch (IOException e) {
            log.error("[crawlFilesystem] Failed to crawl '{}' with embedded crawler", path, e);
        }

        return "OK";
    }

    @RequestMapping(value = "/url", params = {"url"})
    @ResponseBody
    public String crawlUrl(
        @RequestParam(value = "url") String url,
        @RequestParam(value = "edmServerHttpAddress", defaultValue = "http://localhost:8053") String edmServerHttpAddress,
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

    @RequestMapping(value = "/document", method = RequestMethod.POST)
    @ResponseBody
    public EdmDocumentFile create(@RequestBody EdmDocumentFile edmDocument) {
        return edmCrawlingService.save(edmDocument);
    }

}
