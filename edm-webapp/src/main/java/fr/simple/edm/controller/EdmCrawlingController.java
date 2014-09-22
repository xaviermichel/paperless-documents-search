package fr.simple.edm.controller;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;

@Controller
public class EdmCrawlingController {

    private final Logger logger = LoggerFactory.getLogger(EdmCrawlingController.class);
    
    @RequestMapping(value = "/crawl/filesystem", method = RequestMethod.GET, params = {"path"})
    @ResponseStatus(value=HttpStatus.OK)
    @ResponseBody
    public String crawlFilesystem(   
            @RequestParam(value = "path") String path, 
            @RequestParam(value = "edmServerHttpAddress", defaultValue = "127.0.0.1:8053") String edmServerHttpAddress,
            @RequestParam(value = "sourceName", defaultValue = "unmanned source") String sourceName
       ) {
        logger.info("Starting crawling on path : '{}'", path);
        try {
            FilesystemCrawler.importFilesInDir(path, edmServerHttpAddress, sourceName);
        } catch (ClientProtocolException e) {
            logger.error("Failed to crawl '{}' with embedded crawler", path, e);
        }
        return "OK" ;
    }
    
}
