package fr.simple.edm.controller;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
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
    private EdmSourceService edmSourceService;
    
    @Inject
    private EdmSourceMapper edmSourceMapper;
    
    @RequestMapping(method=RequestMethod.POST, value="/source", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmSourceDto create(@RequestBody EdmSourceDto edmDirectoryDto) {
        return edmSourceMapper.boToDto(edmSourceService.save(edmSourceMapper.dtoToBo(edmDirectoryDto)));
    }
    
    @RequestMapping(value = "/source/name/{sourceName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmSourceDto getOneByName(@PathVariable String sourceName) {
        return edmSourceMapper.boToDto(edmSourceService.findOneByName(sourceName));
    }
    
}
