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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.simple.edm.Application;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.model.EdmSource;
import fr.simple.edm.model.EdmDocumentFile;
import fr.simple.edm.model.EdmDocumentSearchResult;
import fr.simple.edm.service.EdmDocumentService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmDocumentServiceTest {

	@Autowired
	private ElasticsearchTestingHelper elasticsearchTestingHelper;
	
	@Autowired
	private EdmNodeService edmNodeService;
	
	@Autowired
	private EdmDocumentService edmDocumentService;
	
	@Autowired
	private EdmCategoryService edmLibraryService;
	
    @Autowired
    private EdmSourceService edmDirectoryService;
    
	
    private EdmDocumentFile docBac;
    private EdmDocumentFile docBrevet;
    private EdmDocumentFile docBacNotes;
    private EdmDocumentFile docLatex;
	
    
	/**
	 * Will destroy and rebuild ES_INDEX
	 */
	@Before
	public void setUp() throws Exception {
		elasticsearchTestingHelper.destroyAndRebuildIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
		
		String targetDirAbsolutePath = System.getProperty("user.dir") + (System.getProperty("user.dir").contains("edm-webapp") ? "" : "/edm-webapp") + "/target/test-classes/";
		
        docBac = new EdmDocumentFile();
        docBac.setName("Diplome du bac");

        docBrevet = new EdmDocumentFile();
        docBrevet.setName("Brevet");

        docBacNotes = new EdmDocumentFile();
        docBacNotes.setName("Notes du bac");

        docLatex = new EdmDocumentFile();
        docLatex.setName("Un template de document");
        // make a copy because moving test file is not acceptable (someone may come after and require this file) ! 
        Files.copy(Paths.get(targetDirAbsolutePath + "demo_pdf.pdf"), Paths.get(targetDirAbsolutePath + "demo_pdf_tmp.pdf"));
        docLatex.setFilename(targetDirAbsolutePath + "demo_pdf_tmp.pdf");
        
        edmDocumentService.save(docBac);
        edmDocumentService.save(docBrevet);
        edmDocumentService.save(docBacNotes);
        edmDocumentService.save(docLatex);
        
        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
	}

	
	private List<EdmDocumentFile> extractDocumentListFromSearchWrapper(List<EdmDocumentSearchResult> edmDocumentSearchResult) {
	    List<EdmDocumentFile> result = new ArrayList<>();
	    for (EdmDocumentSearchResult res : edmDocumentSearchResult) {
	        result.add(res.getEdmDocument());
	    }
	    return result;
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
                docBac, docBrevet
        });

        assertThat(docs).isNotNull();
        assertThat(docs.size()).isEqualTo(attemptedResult.size());
        assertThat(docs).containsAll(attemptedResult);
    }
    
    /**
     * Search test with ending 's' and accent
     */
    @Test
    public void documentWithAccetAndSShouldBeReturned() throws Exception {
        List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplomes"));

        List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[]{
                docBac, docBrevet
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

    @Test
    public void fileShouldFindHisDocumentPath() {
        String filePath = "Documents/Bienvenue/GED.pdf";
        String nodePath = edmDocumentService.filePathToNodePath(filePath);
        
        assertThat(nodePath).isNotNull();
        assertThat(nodePath).isNotEmpty();
        assertThat(nodePath).isEqualTo("Documents/Bienvenue/GED");
    }
    
    // issue #82
    @Test 
    public void documentWithKeywordInPathShouldBeSearchable() throws Exception {
        
        EdmSource trololoDirectory = new EdmSource();
        trololoDirectory.setName("trololo");
        trololoDirectory.setParentId(edmLibraryService.getEdmCategories().get(0).getId());
        trololoDirectory = edmDirectoryService.save(trololoDirectory);
        
        EdmSource yearDirectory = new EdmSource();
        yearDirectory.setName("2014");
        yearDirectory.setParentId(trololoDirectory.getId());
        yearDirectory = edmDirectoryService.save(yearDirectory);
        
        EdmDocumentFile document = new EdmDocumentFile();
        document.setName("echeancier 2014");
        document.setParentId(yearDirectory.getId());
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
    
}
