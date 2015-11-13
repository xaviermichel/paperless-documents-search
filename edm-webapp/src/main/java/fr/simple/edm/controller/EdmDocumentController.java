package fr.simple.edm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javassist.NotFoundException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.domain.EdmDocumentUploadResponse;
import fr.simple.edm.service.EdmDocumentService;

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

    @RequestMapping(value="/document/upload", method=RequestMethod.POST)
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody EdmDocumentUploadResponse executeUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String temporaryFileToken =  String.valueOf(System.currentTimeMillis()) + String.valueOf(Math.random() + "." + fileExtension);

        byte[] bytes = multipartFile.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(edmpTmpdir + temporaryFileToken)));
        stream.write(bytes);
        stream.close();

        EdmDocumentUploadResponse uploadResponse = new EdmDocumentUploadResponse();
        uploadResponse.setTemporaryFileToken(temporaryFileToken);
        return uploadResponse;
    }


    @RequestMapping(method=RequestMethod.POST, value="/document", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmDocumentFile create(@RequestBody EdmDocumentFile edmDocument) {
        if (edmDocument.getTemporaryFileToken() != null) {
            String tmpFileLocation = edmpTmpdir + edmDocument.getTemporaryFileToken();
            edmDocument.setFilename(tmpFileLocation);
        }
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
        response.setContentType(Files.probeContentType(filePath));  
        return new FileSystemResource(new File(edmDocumentFile.getNodePath()));
    }

}
