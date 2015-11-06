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
public class WebConfigTest {

	WebConfig webConfig = new WebConfig();

	@Autowired
	private WebApplicationContext context;

	@Test
	public void edmUploadmMaxFileSizeShouldBeNullByDefault() {
		Object edmUploadmMaxFileSize = ReflectionTestUtils.getField(webConfig,
				"edmUploadmMaxFileSize");
		assertThat(edmUploadmMaxFileSize).isNull();
	}

	@Test
	public void edmUploadMaxRequestSizeShouldBeNullByDefault() {
		Object edmUploadMaxRequestSize = ReflectionTestUtils.getField(
				webConfig, "edmUploadMaxRequestSize");
		assertThat(edmUploadMaxRequestSize).isNull();
	}

}
