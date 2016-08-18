package fr.simple.edm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

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
    
	public static void extractZip(File inputFile, File outputDirectory) throws IOException {
		ZipFile zipFile = new ZipFile(inputFile);
		try {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File entryDestination = new File(outputDirectory, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					InputStream in = zipFile.getInputStream(entry);
					OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					out.close();
				}
			}
		} finally {
			zipFile.close();
		}
	}
}
