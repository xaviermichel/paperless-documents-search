package fr.simple.edm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmAggregationsWrapper;
import fr.simple.edm.domain.EdmAggregationsWrapperDto;
import fr.simple.edm.domain.EdmCategoryAggregationItem;
import fr.simple.edm.domain.EdmCategoryAggregationItemDto;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapperDto;
import fr.simple.edm.domain.EdmSuggestionsWrapper;
import fr.simple.edm.domain.EdmSuggestionsWrapperDto;
import fr.simple.edm.service.EdmAggregationsService;
import fr.simple.edm.service.EdmDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("documents")
@Slf4j
public class EdmDocumentController {

	private final EdmDocumentService edmDocumentService;

	private final EdmAggregationsService edmAggregationsService;

	private final ModelMapper modelMapper;

	public EdmDocumentController(EdmDocumentService edmDocumentService, EdmAggregationsService edmAggregationsService) {
		this.edmDocumentService = edmDocumentService;
		this.edmAggregationsService = edmAggregationsService;
		modelMapper = new ModelMapper();
	}

	@GetMapping
	public EdmDocumentSearchResultWrapperDto search(@RequestParam(value = "q") String pattern) {
		log.debug("Searched pattern : '{}'", pattern);
		return convertToDto(edmDocumentService.search(pattern));
	}

	@PostMapping
	public EdmDocumentFileDto create(@RequestBody EdmDocumentFileDto edmDocumentFileDto) {
		EdmDocumentFile edmDocumentFile = convertToEntity(edmDocumentFileDto);
		edmDocumentFile = edmDocumentService.save(edmDocumentFile);
		return convertToDto(edmDocumentFile);
	}

	@GetMapping(value = "suggest")
	public EdmSuggestionsWrapperDto getSuggestions(@RequestParam(value = "q") String pattern) {
		log.debug("Suggestions pattern : '{}'", pattern);
		return convertToDto(edmAggregationsService.getSuggestions(pattern));
	}

	@GetMapping(value = "top_terms")
	public EdmAggregationsWrapperDto getTerms(@RequestParam(value = "q", defaultValue = "") String pattern,
			@RequestParam(value = "e", defaultValue = "") String exclusionRegex) {
		log.debug("Get relative terms for pattern : '{}'", pattern);
		return convertToDto(edmAggregationsService.getTopTerms(pattern, exclusionRegex));
	}

	@GetMapping(value = "aggregations")
	public Map<String, EdmAggregationsWrapperDto> getAggregations(@RequestParam(value = "q", defaultValue = "") String pattern) {
		log.debug("Get relative terms for pattern : '{}'", pattern);
		Map<String, EdmAggregationsWrapper> aggregations = edmAggregationsService.getAggregations(pattern);
		return convertToDto(aggregations);
	}

	private EdmDocumentFileDto convertToDto(EdmDocumentFile edmDocumentFile) {
		EdmDocumentFileDto edmDocumentFileDto = modelMapper.map(edmDocumentFile, EdmDocumentFileDto.class);
		return edmDocumentFileDto;
	}

	private EdmDocumentFile convertToEntity(EdmDocumentFileDto edmDocumentFileDto) {
		EdmDocumentFile edmDocumentFile = modelMapper.map(edmDocumentFileDto, EdmDocumentFile.class);
		return edmDocumentFile;
	}

	private EdmDocumentSearchResultWrapperDto convertToDto(EdmDocumentSearchResultWrapper edmDocumentSearchResultWrapper) {
		return modelMapper.map(edmDocumentSearchResultWrapper, EdmDocumentSearchResultWrapperDto.class);
	}

	private EdmSuggestionsWrapperDto convertToDto(EdmSuggestionsWrapper edmSuggestionsWrapper) {
		return modelMapper.map(edmSuggestionsWrapper, EdmSuggestionsWrapperDto.class);
	}

	private Map<String, EdmAggregationsWrapperDto> convertToDto(Map<String, EdmAggregationsWrapper> aggregations) {
		Map<String, EdmAggregationsWrapperDto> aggregationsDto = new HashMap<>();
		for (String agg : aggregations.keySet()) {
			if (agg.equals("fileCategory")) {
				aggregationsDto.put(agg, mapFileCategoriesWrapper(aggregations.get(agg)));
			}
			else {
				aggregationsDto.put(agg, modelMapper.map(aggregations.get(agg), EdmAggregationsWrapperDto.class));
			}
		}
		return aggregationsDto;
	}

	private EdmAggregationsWrapperDto mapFileCategoriesWrapper(EdmAggregationsWrapper edmAggregationsWrapper) {
		EdmAggregationsWrapperDto wrapperDto = new EdmAggregationsWrapperDto();
		wrapperDto.setAggregates(new ArrayList<>());
		for (EdmAggregationItem edmAggregationItem : edmAggregationsWrapper.getAggregates()) {
			EdmCategoryAggregationItem edmCategoryAggregationItem = (EdmCategoryAggregationItem) edmAggregationItem;
			EdmCategoryAggregationItemDto edmCategoryAggregationItemDto = modelMapper.map(edmCategoryAggregationItem, EdmCategoryAggregationItemDto.class);
			wrapperDto.getAggregates().add(edmCategoryAggregationItemDto);
		}
		return wrapperDto;
	}

	private EdmAggregationsWrapperDto convertToDto(EdmAggregationsWrapper edmAggregationsWrapper) {
		return modelMapper.map(edmAggregationsWrapper, EdmAggregationsWrapperDto.class);
	}
}
