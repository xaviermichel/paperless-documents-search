package fr.simple.edm.controller;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import fr.simple.edm.common.dto.EdmNodeDto;
import fr.simple.edm.mapper.EdmNodeMapper;
import fr.simple.edm.model.EdmNode;
import fr.simple.edm.service.EdmNodeService;

@RestController
public class EdmNodeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdmNodeController.class);

	@Inject
	private EdmNodeService edmNodeService;

	@Inject
	private EdmNodeMapper edmNodeMapper;

    @RequestMapping(value = "/node/{nodeid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNodeDto read(@PathVariable String nodeid) {
        return edmNodeMapper.boToDto(edmNodeService.findOne(nodeid));
    }

    @RequestMapping(method=RequestMethod.POST, value="/node", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNodeDto create(@RequestBody EdmNodeDto edmNodeDto) {
        return edmNodeMapper.boToDto(edmNodeService.save(edmNodeMapper.dtoToBo(edmNodeDto)));
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/node/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody void delete(@PathVariable String id) {
        edmNodeService.deleteRecursively(edmNodeService.findOne(id));
    }

	// not really restfull
	@RequestMapping(value = "/node/path/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNodeDto read(HttpServletRequest request) {
	    String nodepath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
	    nodepath = nodepath.replaceFirst("/node/path/", "");
	    LOGGER.debug("get node for path : '{}'", nodepath);
        return edmNodeMapper.boToDto(edmNodeService.findOneByPath(nodepath));
    }

	// not really restfull
	@RequestMapping(value = "/node/childs/{nodeid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<EdmNodeDto> getChildNodes(@PathVariable String nodeid) {
	    List<EdmNode> children = edmNodeService.getChildren(nodeid);
	    Collections.sort(children);
		return edmNodeMapper.boToDto(children);
	}

}
