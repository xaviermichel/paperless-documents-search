package fr.simple.edm.util;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

@Slf4j
public class ResourceUtils {
	
    /**
     * @warning returns null if cannot get content
     */
    public static String getContentOfEmbeddedFile(String embeddedPath) {
        try {
        	return FileUtils.readFileToString(org.springframework.util.ResourceUtils.getFile(org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX + embeddedPath), "UTF-8");
        }
        catch (Exception e) {
            log.warn("Failed to get content of file " + embeddedPath, e);
        }
        return null;
    }
}
