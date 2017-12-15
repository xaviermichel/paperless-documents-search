package fr.simple.edm.crawler;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.simple.edm.crawler.url.UrlCrawler;

public class UrlCrawlerTest {

    @Test
    public void emptyPatternShouldNotExcludeDoc() throws Exception {
        String exclusionRegex = "";
        String filePath = "http://data.io/project/.git/config";

        boolean isExcluded = UrlCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isFalse();
    }

    @Test
    public void pathWithExcludedRegexShouldBeIgnored() throws Exception {
        String exclusionRegex = "\\.git";
        String filePath = "http://data.io/project/.git/config";

        boolean isExcluded = UrlCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isTrue();
    }

    @Test
    public void pathWithoutExcludedRegexShouldBeIgnored() throws Exception {
        String exclusionRegex = "\\.svn";
        String filePath = "http://data.io/project/.git/config";

        boolean isExcluded = UrlCrawler.isExcluded(filePath, exclusionRegex);

        assertThat(isExcluded).isFalse();
    }
}
