package fr.simple.edm.service;

import fr.simple.edm.Application;
import fr.simple.edm.EdmTestHelper;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmAutoTidySuggestion;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmDocumentServiceTest {

    @Autowired
    private ElasticsearchTestingHelper elasticsearchTestingHelper;

    @Autowired
    private EdmDocumentService edmDocumentService;

    @Autowired
    private EdmTestHelper edmTestHelper;

    /**
     * Will destroy and rebuild ES_INDEX before each test
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

    private List<EdmDocumentFile> extractDocumentListFromSearchWrapper(EdmDocumentSearchResultWrapper edmDocumentSearchResultWrapper) {
        return edmDocumentSearchResultWrapper.getSearchResults().stream()
                .map(EdmDocumentSearchResult::getEdmDocument)
                .collect(Collectors.toList());
    }

    /**
     * Search on doc name, very basic
     */
    @Test
    public void documentWhichContainsWordShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("brevet"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBrevet()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search test with accented word
     */
    @Test
    public void documentWithAccentShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("brevets"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBrevet()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }


    /**
     * Search test with ending 's'
     */
    @Test
    public void documentWithSShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplômes"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBac()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search test with ending 's' and accent
     */
    @Test
    public void documentWithAccentAndSShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplomes"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBac()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search on multi word
     */
    @Test
    public void documentWitchContainsAllWordsShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplôme bac"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBac()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search on multi word with multi-spaces
     */
    @Test
    public void documentWitchContainsAllWordsWithMultipleSpacesShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplôme    bac"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBac()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search in document binary content
     */
    @Test
    public void searchOnBinaryContent() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("latex"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocLatex()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search in document binary metadata (author)
     */
    @Test
    public void searchOnBinaryMetadataContent() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("xavier"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocLatex()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }


    /**
     * Search use synonyms (02 = février)
     */
    @Test
    public void searchShouldUseSynonym() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("paye février"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocBulletinSalaire()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    @Test
    public void documentWithKeywordInPathShouldBeSearchable() throws Exception {

        EdmDocumentFile document = new EdmDocumentFile();
        document.setName("echeancier 2014");
        document.setNodePath("trololo/2014/echeancier 2014");
        document = edmDocumentService.save(document);

        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);

        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("echeancier trololo 2014"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                document
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    /**
     * Search on doc content, with OCR (text in image)
     */
    @Test
    public void imagedDocumentWhichContainsWordShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("bonjour"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                edmTestHelper.getDocForOcr(), edmTestHelper.getDocSomeBill()
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

    @Test
    public void shouldReturnTheRightDocumentForATextFileToAutoTidy() throws Exception {
        MockMultipartFile dummyFile = new MockMultipartFile("file", "filename.txt", "text/plain", Files.readAllBytes(Paths.get(this.getClass().getResource("/documents/some_bill.pdf.txt").toURI())));

        EdmAutoTidySuggestion edmAutoTidySuggestion = edmDocumentService.getTidySuggestions(dummyFile);

        EdmAutoTidySuggestion expectedEdmAutoTidySuggestion = EdmAutoTidySuggestion.builder().suggestedExtension("pdf").suggestedFileLocation("/documents/").suggestedFileName("some_bill").originalNodePath("/documents/some_bill.pdf").build();
        assertThat(edmAutoTidySuggestion).isNotNull();
        assertThat(edmAutoTidySuggestion).isEqualTo(expectedEdmAutoTidySuggestion);
    }
}
