package fr.simple.edm.controller;

import java.util.List;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmCategoryDto;
import fr.simple.edm.service.EdmCategoryService;
import org.modelmapper.ModelMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("categories")
public class EdmCategoryController {

	private final EdmCategoryService edmCategoryService;

	private final ModelMapper modelMapper;

	public EdmCategoryController(EdmCategoryService edmCategoryService) {
		this.edmCategoryService = edmCategoryService;
		this.modelMapper = new ModelMapper();
	}

	@GetMapping
	public List<EdmCategoryDto> list() {
		return edmCategoryService.findAll().stream().map(this::convertToDto).collect(toList());
	}

	@PostMapping
	public EdmCategoryDto create(@RequestBody EdmCategoryDto edmCategoryDto) {
		EdmCategory edmCategory = convertToEntity(edmCategoryDto);
		edmCategory = edmCategoryService.save(edmCategory);
		return convertToDto(edmCategory);
	}

	@GetMapping(params = { "categoryName" })
	public EdmCategoryDto getOneByName(@RequestParam("categoryName") String categoryName) {
		return convertToDto(edmCategoryService.findOneByName(categoryName));
	}

	private EdmCategoryDto convertToDto(EdmCategory edmCategory) {
		EdmCategoryDto edmCategoryDto = modelMapper.map(edmCategory, EdmCategoryDto.class);
		return edmCategoryDto;
	}

	private EdmCategory convertToEntity(EdmCategoryDto edmCategoryDto) {
		EdmCategory edmCategory = modelMapper.map(edmCategoryDto, EdmCategory.class);
		return edmCategory;
	}
}
