package fr.simple.edm.service;

import fr.simple.edm.Application;
import fr.simple.edm.EdmTestHelper;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmAggregationServiceTest {

    @Autowired
    private ElasticsearchTestingHelper elasticsearchTestingHelper;

    @Autowired
    private EdmAggregationsService edmAggregationsService;

    @Autowired
    private EdmDocumentService edmDocumentService;

    @Autowired
    private EdmTestHelper edmTestHelper;

    /**
     * Will destroy and rebuild ES_INDEX on each test
     */
    @Before
    public void setUp() throws Exception {
        edmTestHelper.destroyAndRebuildElasticContent();
    }

    /**
     * Leave the database after test
     */
    @After
    public void tearDown() throws Exception {
        elasticsearchTestingHelper.deleteAllDocumentsForIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }

    private List<String> extractAggregationValueFromAggregationWrapper(List<EdmAggregationItem> edmAggregationItems) {
        return edmAggregationItems.stream()
                .map(EdmAggregationItem::getKey)
                .collect(Collectors.toList());
    }

    @Test
    public void exclustionRegexShouldBeFilled() {
        String exclusionRegexValue = (String) ReflectionTestUtils.getField(edmAggregationsService, "edmTopTermsExlusionRegex");
        assertThat(exclusionRegexValue).isEqualTo("[a-z]{1,2}|[dlmcs]es|data|docs|documents|edm|files|simple|page");
    }

    @Test
    public void autocompleteShouldSuggestOnDocumentName() throws Exception {
        List<EdmDocumentFile> docs = edmAggregationsService.getSuggestions("dipl");

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBac()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    @Test
    public void autocompleteShouldSuggestOnDocumentPath() throws Exception {
        EdmDocumentFile document = new EdmDocumentFile();
        document.setName("document without name");
        document.setNodePath("without/name/echeancier/document");
        document = edmDocumentService.save(document);

        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);

        List<EdmDocumentFile> docs = edmAggregationsService.getSuggestions("echea");

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                document
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    @Test
    public void extensionShouldBeAggregated() {
        Map<String, List<EdmAggregationItem>> aggregations = edmAggregationsService.getAggregations("paye");
        List<String> extensions = extractAggregationValueFromAggregationWrapper(aggregations.get("fileExtension"));

        List<String> attemptedResult = Arrays.asList(new String[]{
                "pdf"
        });

        assertThat(extensions.size()).isEqualTo(attemptedResult.size());
        assertThat(extensions).containsAll(attemptedResult);
    }
}
