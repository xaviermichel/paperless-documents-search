package fr.simple.edm.service;

import static org.fest.assertions.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import fr.simple.edm.Application;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentSearchResult;
import fr.simple.edm.domain.EdmDocumentSearchResultWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmDocumentServiceTest {

    @Autowired
    private ElasticsearchTestingHelper elasticsearchTestingHelper;

    @Autowired
    private EdmDocumentService edmDocumentService;
    
    private EdmDocumentFile docBac;
    private EdmDocumentFile docBrevet;
    private EdmDocumentFile docBacNotes;
    private EdmDocumentFile docBulletinSalaire;
    private EdmDocumentFile docLatex;

    /**
     * Will destroy and rebuild ES_INDEX
     */
    @Before
    public void setUp() throws Exception {
        elasticsearchTestingHelper.destroyAndRebuildIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);

        docBac = new EdmDocumentFile();
        docBac.setName("Diplome du bac");
        docBac.setNodePath("/documents/1");

        docBrevet = new EdmDocumentFile();
        docBrevet.setName("Brevet");
        docBrevet.setNodePath("/documents/2");

        docBacNotes = new EdmDocumentFile();
        docBacNotes.setName("Notes du bac");
        docBacNotes.setNodePath("/documents/3");

        docBulletinSalaire = new EdmDocumentFile();
        docBulletinSalaire.setName("Bulletin de paye");
        docBulletinSalaire.setNodePath("/salaire/02.pdf");
        docBulletinSalaire.setFileContentType("application/pdf");
        docBulletinSalaire.setFileExtension("pdf");

        docLatex = new EdmDocumentFile();
        docLatex.setName("Un template de document");
        docLatex.setFileContent(Files.readAllBytes(Paths.get(this.getClass().getResource("/demo_pdf.pdf").toURI())));
        docLatex.setFileContentType("application/pdf");
        docLatex.setFileExtension("pdf");
        docLatex.setNodePath("/documents/4");

        docBac = edmDocumentService.save(docBac);
        docBrevet = edmDocumentService.save(docBrevet);
        docBacNotes = edmDocumentService.save(docBacNotes);
        docBulletinSalaire = edmDocumentService.save(docBulletinSalaire);
        docLatex = edmDocumentService.save(docLatex);

        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }


    private List<EdmDocumentFile> extractDocumentListFromSearchWrapper(EdmDocumentSearchResultWrapper edmDocumentSearchResultWrapper) {
        List<EdmDocumentFile> result = new ArrayList<>();
        for (EdmDocumentSearchResult res : edmDocumentSearchResultWrapper.getSearchResults()) {
            result.add(res.getEdmDocument());
        }
        return result;
    }

    
    @Test
    public void exclustionRegexShouldBeFilled() {
        String exclusionRegexValue = (String) ReflectionTestUtils.getField(edmDocumentService, "edmTopTermsExlusionRegex");
        assertThat(exclusionRegexValue).isEqualTo("[a-z]{1,2}|[dlmcs]es|data|docs|documents|edm|files|simple|page");
    }
    
    /**
     * Search on doc name, very basic
     */
    @Test
    public void documentWhichContainsWordShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("brevet"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                docBrevet
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
                docBrevet
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
                docBac
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
                docBac
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
                docBac
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
                docBac
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
                docLatex
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
                docLatex
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
                docBulletinSalaire
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

    @Test
    public void autocompleteShouldSuggestOnDocumentName() throws Exception {
        List<EdmDocumentFile> docs = edmDocumentService.getSuggestions("dipl");

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                docBac
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

        List<EdmDocumentFile> docs = edmDocumentService.getSuggestions("echea");

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                document
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }

}
