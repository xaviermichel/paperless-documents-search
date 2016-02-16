package fr.simple.edm.controller;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.simple.edm.TemplateResourcesConfig;
import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class IndexController {

    @Value("${info.app.name}")
    private String applicationName;

    @Inject
    private TemplateResourcesConfig templateResourcesConfig;
    
    @RequestMapping("/")
    public String home(Model model) {

        // current page
        model.addAttribute("section_document", true);

        // template resource configuration
        model.mergeAttributes(templateResourcesConfig.asMap());

        // application informations
        model.addAttribute("app_name", applicationName);

        log.debug("generating index page for client {}", model.asMap());

        return "home";
    }

}
