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

import fr.simple.edm.common.dto.EdmCategoryDto;
import fr.simple.edm.mapper.EdmCategoryMapper;
import fr.simple.edm.service.EdmCategoryService;

@RestController
public class EdmCategoryController {

	@Inject
	private EdmCategoryService edmCategoryService;
	
	@Inject
	private EdmCategoryMapper edmCategoryMapper;
	
	
    @RequestMapping(value = "/category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<EdmCategoryDto> list() {
        return edmCategoryMapper.boToDto(edmCategoryService.getEdmCategories());
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmCategoryDto create(@RequestBody EdmCategoryDto edmLibraryDto) {
        return edmCategoryMapper.boToDto(edmCategoryService.save(edmCategoryMapper.dtoToBo(edmLibraryDto)));
    }
    
    @RequestMapping(value = "/category/name/{categoryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmCategoryDto getOneByName(@PathVariable String categoryName) {
        return edmCategoryMapper.boToDto(edmCategoryService.findOneByName(categoryName));
    }
}


