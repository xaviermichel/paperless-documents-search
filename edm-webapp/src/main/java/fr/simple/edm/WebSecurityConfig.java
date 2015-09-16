package fr.simple.edm;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@PropertySources(value = { @PropertySource("classpath:/application.properties") })
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Inject
    private Environment env;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    private boolean isAuthConfigured() {
        return ! StringUtils.isEmpty(env.getProperty("edm.crawler.login"));
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        if (! isAuthConfigured()) {
            LOGGER.warn("No 'edm.crawler.login' defined, will not filter /crawl url !");
            return;
        }
        LOGGER.info("configuring http -> '/crawl/**' have to be 'CRAWLER'");
        http
            .authorizeRequests()
              .antMatchers("/crawl/**")
                  .hasAnyRole("CRAWLER")
              .anyRequest()
                  .permitAll()
              
                  .and() // very important for curl !
                  .httpBasic()  
        ;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        if (! isAuthConfigured()) {
            LOGGER.warn("No 'edm.crawler.login' defined, will not configure global auth !");
            return;
        }
        LOGGER.info("configuring auth; adding user {}", env.getProperty("edm.crawler.login"));
        auth
            .inMemoryAuthentication()
                .withUser(env.getProperty("edm.crawler.login")).password(env.getProperty("edm.crawler.pass")).roles("CRAWLER")
        ;
    }
}