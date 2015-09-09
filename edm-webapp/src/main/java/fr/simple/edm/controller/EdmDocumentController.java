package fr.simple.edm.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javassist.NotFoundException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
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

import fr.simple.edm.common.dto.EdmAggregationItemDto;
import fr.simple.edm.common.dto.EdmDocumentFileDto;
import fr.simple.edm.common.dto.EdmDocumentSearchResultWrapperDto;
import fr.simple.edm.common.dto.EdmDocumentUploadResponse;
import fr.simple.edm.mapper.EdmAggregationItemMapper;
import fr.simple.edm.mapper.EdmDocumentMapper;
import fr.simple.edm.mapper.EdmDocumentSearchResultWrapperMapper;
import fr.simple.edm.model.EdmDocumentFile;
import fr.simple.edm.service.EdmDocumentService;

@RestController
@PropertySources(value = {
        @PropertySource("classpath:/application.properties")
})
public class EdmDocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdmDocumentController.class);

    @Inject
    private Environment env;

    @Inject
    private EdmDocumentService edmDocumentService;

    @Inject
    private EdmDocumentMapper edmDocumentMapper;

    @Inject
    private EdmAggregationItemMapper edmAggregationItemMapper;

    @Inject
    private EdmDocumentSearchResultWrapperMapper edmDocumentSearchResultWrapperMapper;

    @RequestMapping(value = "/document", method = RequestMethod.GET, params = {"q"})
    public @ResponseBody EdmDocumentSearchResultWrapperDto search(@RequestParam(value = "q") String pattern) {
        LOGGER.debug("Searched pattern : '{}'", pattern);
        return edmDocumentSearchResultWrapperMapper.boToDto(edmDocumentService.search(pattern));
    }

    @RequestMapping(value = "/document/suggest", method = RequestMethod.GET, params = {"q"})
    public @ResponseBody List<EdmDocumentFileDto> getSuggestions(@RequestParam(value = "q") String pattern) {
        LOGGER.debug("Suggestions pattern : '{}'", pattern);
        return edmDocumentMapper.boToDto(edmDocumentService.getSuggestions(pattern));
    }

    @RequestMapping(value = "/document/top_terms", method = RequestMethod.GET)
    public @ResponseBody List<EdmAggregationItemDto> getTerms(@RequestParam(value = "q", defaultValue = "") String pattern) {
        LOGGER.debug("Get relative terms for pattern : '{}'", pattern);
        return edmAggregationItemMapper.boToDto(edmDocumentService.getTopTerms(pattern));
    }

    @RequestMapping(value = "/document/aggregations", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<EdmAggregationItemDto>> getAggregations(@RequestParam(value = "q", defaultValue = "") String pattern) {
        LOGGER.debug("Get relative terms for pattern : '{}'", pattern);
        return edmAggregationItemMapper.boToDto(edmDocumentService.getAggregations(pattern));
    }

    @RequestMapping(value="/document/upload", method=RequestMethod.POST , headers = "content-type=multipart/*")
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody EdmDocumentUploadResponse executeUpload(@RequestParam(value = "file", required = true) MultipartFile multipartFile, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String fileExtension = com.google.common.io.Files.getFileExtension(multipartFile.getOriginalFilename());
        String temporaryFileToken =  String.valueOf(System.currentTimeMillis()) + String.valueOf(Math.random() + "." + fileExtension);

        byte[] bytes = multipartFile.getBytes();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(env.getProperty("edm.tmpdir") + temporaryFileToken)));
        stream.write(bytes);
        stream.close();

        EdmDocumentUploadResponse uploadResponse = new EdmDocumentUploadResponse();
        uploadResponse.setTemporaryFileToken(temporaryFileToken);
        return uploadResponse;
    }


    @RequestMapping(method=RequestMethod.POST, value="/document", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmDocumentFileDto create(@RequestBody EdmDocumentFileDto edmDocument) {
        EdmDocumentFile document = edmDocumentMapper.dtoToBo(edmDocument);
        if (edmDocument.getTemporaryFileToken() != null) {
            String tmpFileLocation = env.getProperty("edm.tmpdir") + edmDocument.getTemporaryFileToken();
            document.setFilename(tmpFileLocation);
        }
        return edmDocumentMapper.boToDto(edmDocumentService.save(document));
    }
    
    @RequestMapping(value = "/files", method = RequestMethod.GET, params = {"docId"})
    public @ResponseBody FileSystemResource getFile(@RequestParam(value = "docId") String docId, HttpServletResponse response) throws NotFoundException, IOException {
        LOGGER.debug("Downloading file : '{}'", docId);
        
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
