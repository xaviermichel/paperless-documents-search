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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.service.EdmDocumentService;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class EdmDocumentController {

    @Value("${edm.tmpdir}")
    private String edmpTmpdir;

    @Inject
    private EdmDocumentService edmDocumentService;

    @RequestMapping(value = "/document", method = RequestMethod.GET, params = {"q"})
    public @ResponseBody EdmDocumentSearchResultWrapper search(@RequestParam(value = "q") String pattern) {
        log.debug("Searched pattern : '{}'", pattern);
        return edmDocumentService.search(pattern);
    }

    @RequestMapping(value = "/document/suggest", method = RequestMethod.GET, params = {"q"})
    public @ResponseBody List<EdmDocumentFile> getSuggestions(@RequestParam(value = "q") String pattern) {
        log.debug("Suggestions pattern : '{}'", pattern);
        return edmDocumentService.getSuggestions(pattern);
    }

    @RequestMapping(value = "/document/top_terms", method = RequestMethod.GET)
    public @ResponseBody List<EdmAggregationItem> getTerms(@RequestParam(value = "q", defaultValue = "") String pattern) {
        log.debug("Get relative terms for pattern : '{}'", pattern);
        return edmDocumentService.getTopTerms(pattern);
    }

    @RequestMapping(value = "/document/aggregations", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<EdmAggregationItem>> getAggregations(@RequestParam(value = "q", defaultValue = "") String pattern) {
        log.debug("Get relative terms for pattern : '{}'", pattern);
        return edmDocumentService.getAggregations(pattern);
    }

    @RequestMapping(method=RequestMethod.POST, value="/document", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmDocumentFile create(@RequestBody EdmDocumentFile edmDocument) {
        return edmDocumentService.save(edmDocument);
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET, params = {"docId"})
    public @ResponseBody FileSystemResource getFile(@RequestParam(value = "docId") String docId, HttpServletResponse response) throws NotFoundException, IOException {
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
