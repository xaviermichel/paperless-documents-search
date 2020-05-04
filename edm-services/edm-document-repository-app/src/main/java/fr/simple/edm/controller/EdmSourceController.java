package fr.simple.edm.controller;

import java.util.List;

import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.domain.EdmSourceDto;
import fr.simple.edm.service.EdmSourceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("sources")
@Slf4j
public class EdmSourceController {

	private final EdmSourceService edmSourceService;

	private final ModelMapper modelMapper;

	public EdmSourceController(EdmSourceService edmSourceService) {
		this.edmSourceService = edmSourceService;
		modelMapper = new ModelMapper();
	}

	@GetMapping
	public List<EdmSourceDto> list() {
		return edmSourceService.findAll().stream().map(this::convertToDto).collect(toList());
	}

	@PostMapping
	public EdmSourceDto create(@RequestBody EdmSourceDto edmSourceDto) {
		EdmSource edmSource = convertToEntity(edmSourceDto);
		edmSource = edmSourceService.save(edmSource);
		return convertToDto(edmSource);
	}

	@GetMapping(params = { "sourceName" })
	public EdmSourceDto getOneByName(@RequestParam("sourceName") String sourceName) {
		return convertToDto(edmSourceService.findOneByName(sourceName));
	}

	@GetMapping(value = "start-crawling", params = { "source" })
	@ResponseStatus(value = HttpStatus.OK)
	public void startCrawling(@RequestParam(value = "source") String source) {
		log.info("Begin crawling for source : {}", source);
		edmSourceService.markCrawlingStart(source);
	}

	@RequestMapping(value = "finish-crawling", params = { "source" })
	@ResponseStatus(value = HttpStatus.OK)
	public void stopCrawling(@RequestParam(value = "source") String source) {
		log.info("End of crawling for source : {}", source);
		edmSourceService.markCrawlingEnd(source);
	}

	private EdmSourceDto convertToDto(EdmSource edmSource) {
		EdmSourceDto edmSourceDto = modelMapper.map(edmSource, EdmSourceDto.class);
		return edmSourceDto;
	}

	private EdmSource convertToEntity(EdmSourceDto edmSourceDto) {
		EdmSource edmSource = modelMapper.map(edmSourceDto, EdmSource.class);
		return edmSource;
	}
}
