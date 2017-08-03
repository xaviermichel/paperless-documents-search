package fr.simple.edm.controller;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.service.EdmCategoryService;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class EdmCategoryController {

    @Inject
    @Setter
    private EdmCategoryService edmCategoryService;

    @RequestMapping(value = "/category")
    @ResponseBody
    public List<EdmCategory> list() {
        return edmCategoryService.findAll();
    }

    @RequestMapping(value="/category", method = RequestMethod.POST)
    @ResponseBody
    public EdmCategory create(@RequestBody EdmCategory edmCategory) {
        return edmCategoryService.save(edmCategory);
    }

    @RequestMapping(value = "/category/name/{categoryName}")
    @ResponseBody
    public EdmCategory getOneByName(@PathVariable String categoryName) {
        return edmCategoryService.findOneByName(categoryName);
    }
}


