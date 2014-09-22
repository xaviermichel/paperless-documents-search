package fr.simple.edm.controller;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.simple.edm.common.dto.EdmSourceDto;
import fr.simple.edm.mapper.EdmSourceMapper;
import fr.simple.edm.service.EdmSourceService;

@RestController
public class EdmSourceController {

	@Inject
	private EdmSourceService edmDirectoryService;
	
	@Inject
	private EdmSourceMapper edmDirectoryMapper;
	
    @RequestMapping(method=RequestMethod.POST, value="/directory", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmSourceDto create(@RequestBody EdmSourceDto edmDirectoryDto) {
        return edmDirectoryMapper.boToDto(edmDirectoryService.save(edmDirectoryMapper.dtoToBo(edmDirectoryDto)));
    }
	
}
