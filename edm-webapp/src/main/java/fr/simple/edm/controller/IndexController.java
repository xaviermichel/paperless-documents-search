package fr.simple.edm.controller;

import javax.inject.Inject;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@PropertySources(value = {
		@PropertySource("classpath:/properties/constants.properties")
	}
)
public class IndexController {

    @Inject
    private Environment env;
	
    @RequestMapping("/")
    public String home(Model model, @RequestParam(value = "debug", defaultValue = "") String debug) {
    	
        // current page
        model.addAttribute("section_document", true);
    	
        // debug mode flag
        model.addAttribute("debug", ! debug.isEmpty());
        
        // application informations
    	model.addAttribute("APPLICATION_NAME", env.getProperty("APPLICATION_NAME"));
    	model.addAttribute("APPLICATION_ENV", env.getProperty("APPLICATION_ENV"));
        
    	return "home";
    }

}
