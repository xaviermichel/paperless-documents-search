package fr.simple.edm.controller;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

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

import fr.simple.edm.domain.EdmNode;
import fr.simple.edm.service.EdmNodeService;

@RestController
@Slf4j
public class EdmNodeController {

    @Inject
    private EdmNodeService edmNodeService;

    @RequestMapping(value = "/node/{nodeid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNode read(@PathVariable String nodeid) {
        return edmNodeService.findOne(nodeid);
    }

    @RequestMapping(method=RequestMethod.POST, value="/node", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNode create(@RequestBody EdmNode edmNode) {
        return edmNodeService.save(edmNode);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="/node/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public @ResponseBody void delete(@PathVariable String id) {
        edmNodeService.deleteRecursively(edmNodeService.findOne(id));
    }

    // not really restfull
    @RequestMapping(value = "/node/path/**", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody EdmNode read(HttpServletRequest request) {
        String nodepath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        nodepath = nodepath.replaceFirst("/node/path/", "");
        log.debug("get node for path : '{}'", nodepath);
        return edmNodeService.findOneByPath(nodepath);
    }

    // not really restfull
    @RequestMapping(value = "/node/childs/{nodeid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<EdmNode> getChildNodes(@PathVariable String nodeid) {
        List<EdmNode> children = edmNodeService.getChildren(nodeid);
        Collections.sort(children);
        return children;
    }

}
