package fr.simple.edm.service;

import fr.simple.edm.Application;
import fr.simple.edm.EdmTestHelper;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmAggregationsWrapper;
import fr.simple.edm.domain.EdmAggregationItem;
import fr.simple.edm.domain.EdmDocumentFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
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
@ComponentScan(basePackages = {"fr.simple.edm"})
@ActiveProfiles("test")
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
        elasticsearchTestingHelper.deleteAllDocuments();
        elasticsearchTestingHelper.flushIndexes();
    }

    private List<String> extractAggregationValueFromAggregationWrapper(EdmAggregationsWrapper edmAggregationsWrapper) {
        return edmAggregationsWrapper.getAggregates().stream()
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
        List<EdmDocumentFile> docs = edmAggregationsService.getSuggestions("dipl").getDocuments();

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

        elasticsearchTestingHelper.deleteAllDocuments();
        document = edmDocumentService.save(document);
        elasticsearchTestingHelper.flushIndexes();

        List<EdmDocumentFile> docs = edmAggregationsService.getSuggestions("echea").getDocuments();

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
            document
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    @Test
    public void suggestionShouldSuggestSomething() throws Exception {
        EdmDocumentFile document = new EdmDocumentFile();
        document.setName("la data interessant");
        document.setNodePath("without/name/echeancier/docs/a_doc.pdf");
        document.setFileExtension("pdf");

        elasticsearchTestingHelper.deleteAllDocuments();
        document = edmDocumentService.save(document);
        elasticsearchTestingHelper.flushIndexes();

        List<String> topTerms = extractAggregationValueFromAggregationWrapper(edmAggregationsService.getTopTerms(null));

        assertThat(topTerms).isNotNull();
        assertThat(topTerms).contains("echeancier");
        assertThat(topTerms).doesNotContain("interessant", "data", "la", "pdf");
    }

    @Test
    public void extensionShouldBeAggregated() {
        Map<String, EdmAggregationsWrapper> aggregations = edmAggregationsService.getAggregations("paye");
        List<String> extensions = extractAggregationValueFromAggregationWrapper(aggregations.get("fileExtension"));

        List<String> attemptedResult = Arrays.asList(new String[]{
            "pdf"
        });

        assertThat(extensions.size()).isEqualTo(attemptedResult.size());
        assertThat(extensions).containsAll(attemptedResult);
    }

    @Test
    public void categoryShouldBeAggregated() {
        Map<String, EdmAggregationsWrapper> aggregations = edmAggregationsService.getAggregations("paye OR brevet OR bac");
        List<EdmAggregationItem> aggregates = aggregations.get("fileCategory").getAggregates();

        List<EdmAggregationItem> attemptedResult = Arrays.asList(new EdmAggregationItem[]{
            new EdmAggregationItem("category 1", 3),
            new EdmAggregationItem("category 2", 1)
        });

        assertThat(aggregates.size()).isEqualTo(attemptedResult.size());
        assertThat(aggregates).containsAll(attemptedResult);
    }
}
