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

@RestController
public class EdmCategoryController {

    @Inject
    private EdmCategoryService edmCategoryService;

    @RequestMapping(value = "/category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<EdmCategory> list() {
        return edmCategoryService.getEdmCategories();
    }

    @RequestMapping(method=RequestMethod.POST, value="/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmCategory create(@RequestBody EdmCategory edmLibrary) {
        return edmCategoryService.save(edmLibrary);
    }

    @RequestMapping(value = "/category/name/{categoryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmCategory getOneByName(@PathVariable String categoryName) {
        return edmCategoryService.findOneByName(categoryName);
    }
}


