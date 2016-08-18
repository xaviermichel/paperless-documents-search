package fr.simple.edm.controller;

import java.util.List;

import javax.inject.Inject;

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

    @RequestMapping(value = "/source")
    @ResponseBody
    public List<EdmSource> list() {
        return edmSourceService.findAll();
    }

    @RequestMapping(value="/source", method=RequestMethod.POST)
    @ResponseBody
    public EdmSource create(@RequestBody EdmSource edmDirectory) {
        return edmSourceService.save(edmDirectory);
    }

    @RequestMapping(value = "/source/name/{sourceName}")
    @ResponseBody
    public EdmSource getOneByName(@PathVariable String sourceName) {
        return edmSourceService.findOneByName(sourceName);
    }

}
