package fr.simple.edm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = {"fr.simple.edm"})
public class WebSecurityConfigTest {

    WebSecurityConfig webSecurityConfig = new WebSecurityConfig();

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
    public void authentificationShouldBeDisabledByDefault() {
        Boolean isAuthConfigured = ReflectionTestUtils.invokeMethod(
                webSecurityConfig, "isAuthConfigured");
        assertThat(isAuthConfigured).isFalse();
    }

}
