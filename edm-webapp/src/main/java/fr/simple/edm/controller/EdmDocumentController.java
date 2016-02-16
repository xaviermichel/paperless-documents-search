package fr.simple.edm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.service.EdmAggregationsService;
import fr.simple.edm.service.EdmDocumentService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EdmDocumentController {

    @Inject
    private EdmDocumentService edmDocumentService;

    @Inject
    private EdmAggregationsService edmAggregationsService;
    
    @RequestMapping(value = "/document", method = RequestMethod.GET, params = {"q"})
    @ResponseBody
    public EdmDocumentSearchResultWrapper search(@RequestParam(value = "q") String pattern) {
        log.debug("Searched pattern : '{}'", pattern);
        return edmDocumentService.search(pattern);
    }

    @RequestMapping(value = "/document/suggest", method = RequestMethod.GET, params = {"q"})
    @ResponseBody
    public List<EdmDocumentFile> getSuggestions(@RequestParam(value = "q") String pattern) {
        log.debug("Suggestions pattern : '{}'", pattern);
        return edmAggregationsService.getSuggestions(pattern);
    }

    @RequestMapping(value = "/document/top_terms", method = RequestMethod.GET)
    @ResponseBody
    public List<EdmAggregationItem> getTerms(@RequestParam(value = "q", defaultValue = "") String pattern) {
        log.debug("Get relative terms for pattern : '{}'", pattern);
        return edmAggregationsService.getTopTerms(pattern);
    }

    @RequestMapping(value = "/document/aggregations", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<EdmAggregationItem>> getAggregations(@RequestParam(value = "q", defaultValue = "") String pattern) {
        log.debug("Get relative terms for pattern : '{}'", pattern);
        return edmAggregationsService.getAggregations(pattern);
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET, params = {"docId"})
    @ResponseBody
    public FileSystemResource getFile(@RequestParam(value = "docId") String docId, HttpServletResponse response) throws NotFoundException, IOException {
        log.debug("Downloading file : '{}'", docId);

        // document does not exists or access is not allowed
        EdmDocumentFile edmDocumentFile = edmDocumentService.findOne(docId);
        if (edmDocumentFile == null) {
            throw new NotFoundException(docId);
        }

        Path filePath = Paths.get(edmDocumentFile.getNodePath());
        File file = new File(edmDocumentFile.getNodePath());

        response.setContentType(Files.probeContentType(filePath));
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
        return new FileSystemResource(file);
    }

}
