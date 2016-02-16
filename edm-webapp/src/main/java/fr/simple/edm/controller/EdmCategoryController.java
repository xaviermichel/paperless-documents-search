package fr.simple.edm.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.service.EdmCategoryService;
import lombok.Setter;

@RestController
public class EdmCategoryController {

    @Inject
    @Setter
    private EdmCategoryService edmCategoryService;

    @RequestMapping(value = "/category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EdmCategory> list() {
        return edmCategoryService.findAll();
    }

    @RequestMapping(method=RequestMethod.POST, value="/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EdmCategory create(@RequestBody EdmCategory edmCategory) {
        return edmCategoryService.save(edmCategory);
    }

    @RequestMapping(value = "/category/name/{categoryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EdmCategory getOneByName(@PathVariable String categoryName) {
        return edmCategoryService.findOneByName(categoryName);
    }
}


