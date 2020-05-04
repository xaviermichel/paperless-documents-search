package fr.simple.edm.service;

import fr.simple.edm.DocumentRepositoryApplication;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmCategory;
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
public class EdmLibraryServiceTest {

	@Autowired
	private EdmCategoryService edmCategoryService;

	@Autowired
	private ElasticsearchTestingHelper elasticsearchTestingHelper;

	private EdmCategory edmCategory;

	/**
	 * Will destroy and rebuild ES_INDEX
	 */
	@Before
	public void setUp() throws Exception {
		elasticsearchTestingHelper.deleteAllDocuments();
		elasticsearchTestingHelper.flushIndexes();

		EdmCategory category = new EdmCategory();
		category.setName("My category");
		edmCategory = edmCategoryService.save(category);

		elasticsearchTestingHelper.flushIndexes();
	}

	/**
	 * Leave the database after test
	 */
	@After
	public void tearDown() throws Exception {
		elasticsearchTestingHelper.deleteAllDocuments();
		elasticsearchTestingHelper.flushIndexes();
	}

	@Test
	public void categoryCanBeFindByHisName() {
		EdmCategory node = edmCategoryService.findOneByName("My category");
		assertThat(node).isNotNull();
		assertThat(node.getId()).isEqualTo(edmCategory.getId());
	}
}
