package fr.simple.edm.controller;

import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.service.EdmSourceService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class EdmSourceController {

    @Inject
    private EdmSourceService edmSourceService;

    @RequestMapping(value = "/source")
    @ResponseBody
    public List<EdmSource> list() {
        return edmSourceService.findAll();
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST)
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
