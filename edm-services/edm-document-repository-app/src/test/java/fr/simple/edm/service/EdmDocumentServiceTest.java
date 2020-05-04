package fr.simple.edm.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.simple.edm.DocumentRepositoryApplication;
import fr.simple.edm.EdmTestHelper;
import fr.simple.edm.ElasticsearchTestingHelper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DocumentRepositoryApplication.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
@ActiveProfiles("test")
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
		elasticsearchTestingHelper.deleteAllDocuments();
		elasticsearchTestingHelper.flushIndexes();
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
	public void documentWhichContainsWordShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("brevet"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void documentWithAccentShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("brevets"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void documentWithSShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplômes"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void documentWithAccentAndSShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplomes"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void documentWitchContainsAllWordsShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplôme bac"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void documentWitchContainsAllWordsWithMultipleSpacesShouldBeReturned() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("diplôme    bac"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void searchOnBinaryContent() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("latex"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void searchOnMetadataContent() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("titouan"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
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
	public void searchShouldUseSynonym() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("février"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
				edmTestHelper.getDocBulletinSalaire()
		});

		assertThat(docs).isNotNull();
		assertThat(docs.size()).isEqualTo(attemptedResult.size());
		assertThat(docs).containsAll(attemptedResult);
	}

	@Test
	public void searchShouldUseMultiFields() {
		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("template latex"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
				edmTestHelper.getDocLatex()
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

		elasticsearchTestingHelper.flushIndexes();

		List<EdmDocumentFile> docs = extractDocumentListFromSearchWrapper(edmDocumentService.search("echeancier trololo 2014"));

		List<EdmDocumentFile> attemptedResult = Arrays.asList(new EdmDocumentFile[] {
				document
		});

		assertThat(docs).isNotNull();
		assertThat(docs.size()).isEqualTo(attemptedResult.size());
		assertThat(docs).containsAll(attemptedResult);
	}

}
