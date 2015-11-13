package fr.simple.edm.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    @Value("${info.app.name}")
    private String applicationName;
    
    @Value("${info.app.env}")
    private String applicationEnv;
    
    @RequestMapping("/")
    public String home(Model model, @RequestParam(value = "debug", defaultValue = "") String debug) {
        
        // current page
        model.addAttribute("section_document", true);
        
        // debug mode flag
        model.addAttribute("debug", ! debug.isEmpty());
        
        // application informations
        model.addAttribute("APPLICATION_NAME", applicationName);
        model.addAttribute("APPLICATION_ENV", applicationEnv);
        
        return "home";
    }

}
