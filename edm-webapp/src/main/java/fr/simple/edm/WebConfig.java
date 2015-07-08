package fr.simple.edm;

import javax.inject.Inject;
import javax.servlet.MultipartConfigElement;

import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@EnableWebMvc
@Configuration
@PropertySources(value = {
        @PropertySource("classpath:/edm-configuration.properties")
    }
)
public class WebConfig extends WebMvcConfigurerAdapter {

    @Inject
    Environment env;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        // NB, selecting HTML5 as the template mode.
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        resolver.setOrder(1);
        return resolver;
    }
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/properties/messages");
        // if true, the key of the message will be displayed if the key is not
        // found, instead of throwing a NoSuchMessageException
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        // # -1 : never reload, 0 always reload
        messageSource.setCacheSeconds(0);
        return messageSource;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        MultipartResolver multipartResolver = new CommonsMultipartResolver();
        return multipartResolver;
    }
    
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(env.getProperty("edm.upload.maxFileSize"));
        factory.setMaxRequestSize(env.getProperty("edm.upload.maxRequestSize"));
        return factory.createMultipartConfig();
    }
}
