package fr.simple.edm;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Value("${edm.crawler.login:#{null}}")
    private String edmCrawlerLogin;
    
    @Value("${edm.crawler.pass:#{null}}")
    private String edmCrawlerPassword;
    
    private boolean isAuthConfigured() {
        return ! StringUtils.isEmpty(edmCrawlerLogin);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        if (! isAuthConfigured()) {
            log.warn("No 'edm.crawler.login' defined, will not filter /crawl url !");
            return;
        }
        log.info("configuring http -> '/crawl/**' have to be 'CRAWLER'");
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
            log.warn("No 'edm.crawler.login' defined, will not configure global auth !");
            return;
        }
        log.info("configuring auth : adding user {}", edmCrawlerLogin);
        auth
            .inMemoryAuthentication()
                .withUser(edmCrawlerLogin).password(edmCrawlerPassword).roles("CRAWLER")
        ;
    }
}
