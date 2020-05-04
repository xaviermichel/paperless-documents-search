package fr.simple.edm.utils;

import java.io.IOException;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ResourceUtilsTest {

	@Test
	public void embeddedFileContentShouldBeLoaded() throws IOException {
		String fileContent = ResourceUtils.getContent("resourceUtilsTest.txt");
		assertThat(fileContent).isEqualTo("This is a simple content for a test");
	}

	@Test(expected = IOException.class)
	public void notExistingFileShouldThrowException() throws IOException {
		ResourceUtils.getContent("not_existing_file.txt");
	}
}
