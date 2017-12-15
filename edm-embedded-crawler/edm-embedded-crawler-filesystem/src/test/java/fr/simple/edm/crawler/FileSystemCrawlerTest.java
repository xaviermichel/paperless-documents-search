package fr.simple.edm.crawler;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.simple.edm.crawler.filesystem.FilesystemCrawler;

public class FileSystemCrawlerTest {

    @Test
    public void emptyPatternShouldNotExcludeDoc() throws Exception {
        String exclusionRegex = "";
        String filePath = "/data/project/.git/config";

        boolean isExcluded = FilesystemCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isFalse();
    }

    @Test
    public void pathWithExcludedRegexShouldBeIgnored() throws Exception {
        String exclusionRegex = "\\.git";
        String filePath = "/data/project/.git/config";

        boolean isExcluded = FilesystemCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isTrue();
    }

    @Test
    public void pathWithoutExcludedRegexShouldBeIgnored() throws Exception {
        String exclusionRegex = "\\.svn";
        String filePath = "/data/project/.git/config";

        boolean isExcluded = FilesystemCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isFalse();
    }
}
