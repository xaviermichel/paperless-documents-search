package fr.simple.edm;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class WebSecurityConfigTest {

    WebSecurityConfig webSecurityConfig = new WebSecurityConfig();

    @Autowired
    private WebApplicationContext context;

    @Test
    public void crawlerLoginShouldBeNullByDefault() {
        Object defaultLogin = ReflectionTestUtils.getField(webSecurityConfig,
                "edmCrawlerLogin");
        assertThat(defaultLogin).isNull();
    }

    @Test
    public void crawlerPasswordShouldBeNullByDefault() {
        Object defaultPassword = ReflectionTestUtils.getField(
                webSecurityConfig, "edmCrawlerPassword");
        assertThat(defaultPassword).isNull();
    }

    @Test
    public void authentificatonShouldBeDisabledByDefault() {
        Boolean isAuthConfigured = ReflectionTestUtils.invokeMethod(
                webSecurityConfig, "isAuthConfigured");
        assertThat(isAuthConfigured).isFalse();
    }

}
