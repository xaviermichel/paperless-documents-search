package fr.simple.edm.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.MediaType;
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
	
	
    @RequestMapping(value = "/library", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<EdmCategoryDto> list() {
        return edmCategoryMapper.boToDto(edmCategoryService.getEdmLibraries());
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/library", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmCategoryDto create(@RequestBody EdmCategoryDto edmLibraryDto) {
        return edmCategoryMapper.boToDto(edmCategoryService.save(edmCategoryMapper.dtoToBo(edmLibraryDto)));
    }
}
