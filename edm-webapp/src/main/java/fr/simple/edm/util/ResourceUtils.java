package fr.simple.edm.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ResourceUtils {

    public static String getContent(String path) throws IOException {
        try {
            URL url = getResource(path);
            if (url == null) {
                throw new IOException("file was not found in classloader path");
            }
            return IOUtils.toString(url, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            log.warn("Failed to get content of file " + path, e);
            throw e;
        }
    }


    private static URL getResource(String resourceName) {
        ClassLoader loader = ObjectUtils.firstNonNull(Thread.currentThread().getContextClassLoader(), ResourceUtils.class.getClassLoader());
        URL url = loader.getResource(resourceName);
        if (url == null) {
            log.warn("Resource '{}' not found", resourceName);
        }
        return url;
    }
}
