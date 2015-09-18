package fr.simple.edm.crawler.url;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.simple.edm.common.EdmNodeType;
import fr.simple.edm.common.dto.EdmDocumentFileDto;
import fr.simple.edm.crawler.bridge.EdmConnector;


/**
 * Crawl a ressource which is behind an url
 * 
 * @author xavier
 * 
 */
public class UrlCrawler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCrawler.class);
    
    private static final EdmConnector edmConnector = new EdmConnector();
    
    /**
     * 
     * @param url             The path of the directory to crawl
     *                             For example : https://raw.githubusercontent.com/xaviermichel/simple-data-search/master/edm-webapp/src/test/resources/demo_pdf.pdf
     * @param edmServerHttpAddress The address of the EDM webapp HTTP address
     *                             For example : 127.0.0.1:8053
     * @param sourceName           A unique name for this source of documents
     *                             For example : 
     * @param exclusionRegex       Documents names which match with this regex will be ignored
     * 
     * @throws IOException 
     */
    public static void importFilesAtUrl(String url, final String edmServerHttpAddress, final String sourceName, final String categoryName, final String exclusionRegex) throws IOException {
        // create parents
        String categoryId = edmConnector.getIdFromCategoryByCategoryName(edmServerHttpAddress, categoryName);
        String sourceId = edmConnector.getIdFromSourceBySourceName(edmServerHttpAddress, sourceName, categoryId);
        
        // index
        LOGGER.debug("The source ID is {}", sourceId);
        edmConnector.notifyStartCrawling(edmServerHttpAddress, sourceName);
        importFilesAtUrl(url, edmServerHttpAddress, sourceId, exclusionRegex);
        edmConnector.notifyEndOfCrawling(edmServerHttpAddress, sourceName);
    }
    
    public static boolean isExcluded(String filePath, String exclusionPattern) {
        boolean toExclude = !exclusionPattern.isEmpty() && Pattern.compile(exclusionPattern).matcher(filePath).find();
        LOGGER.debug("Check if '{}' match with '{}' : {}", filePath, exclusionPattern, toExclude);
        return toExclude;
    }

    private static void importFilesAtUrl(String url, final String edmServerHttpAddress, final String sourceId, final String exclusionRegex) throws IOException {

        LOGGER.info("Embedded crawler looks for : " + url);

        // exclusion pattern
        if (isExcluded(url, exclusionRegex)) {
            LOGGER.info("File excluded because it matches with exclusion regex");
            return;
        }
        
        // Download the file
        File file = File.createTempFile(url.replaceFirst("[.][^.]+$", ""), "." + FilenameUtils.getExtension(url)); 
        FileUtils.copyURLToFile(new URL(url), file);
       
        // add files
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);

        if (megabytes > 100) {
            LOGGER.warn("Skipping too big file ({})", url);
            Files.delete(file.toPath());
            return;
        }
        
        // upload the file
        String fileToken = edmConnector.uploadFile(edmServerHttpAddress, file);

        // construct DTO
        EdmDocumentFileDto document = new EdmDocumentFileDto();
        document.setDate(new Date(file.lastModified()));
        document.setNodePath(url);
        document.setEdmNodeType(EdmNodeType.DOCUMENT);
        document.setName(url.replaceFirst("[.][^.]+$", ""));
        document.setParentId(sourceId);
        document.setTemporaryFileToken(fileToken);

        // save DTO
        edmConnector.saveEdmDocument(edmServerHttpAddress, document);
        
        // cleaning
        Files.delete(file.toPath());
    }

}
