package fr.simple.edm.controller;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.service.EdmSourceService;

@RestController
public class EdmSourceController {

    @Inject
    private EdmSourceService edmSourceService;
    
    @RequestMapping(method=RequestMethod.POST, value="/source", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmSource create(@RequestBody EdmSource edmDirectory) {
        return edmSourceService.save(edmDirectory);
    }
    
    @RequestMapping(value = "/source/name/{sourceName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmSource getOneByName(@PathVariable String sourceName) {
        return edmSourceService.findOneByName(sourceName);
    }
    
}
