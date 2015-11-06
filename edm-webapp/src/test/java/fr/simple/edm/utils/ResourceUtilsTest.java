package fr.simple.edm.utils;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.simple.edm.util.ResourceUtils;

public class ResourceUtilsTest {

    @Test
    public void embeddedFileContentShouldBeLoaded() {
        String fileContent = ResourceUtils.getContentOfEmbeddedFile("resourceUtilsTest.txt");
        assertThat(fileContent).isEqualTo("This is a simple content for a test");
    }
	
}
