package fr.simple.edm.service;

import static org.fest.assertions.api.Assertions.assertThat;

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
import fr.simple.edm.model.EdmCategory;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { Application.class })
@ComponentScan(basePackages = { "fr.simple.edm" })
public class GedLibraryServiceTest {

	@Autowired
	private EdmCategoryService edmLibraryService;

	@Autowired
	private ElasticsearchTestingHelper elasticsearchTestingHelper;
	
	
	/**
	 * Will destroy and rebuild ES_INDEX
	 */
	@Before
	public void setUp() throws Exception {
		elasticsearchTestingHelper.destroyAndRebuildIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
		elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
	}
	
	@Test
	public void defaultLibraryIsCreatedAtStart() {
		List<EdmCategory> librairies = edmLibraryService.getEdmLibraries();
		
		assertThat(librairies.size()).isEqualTo(1);
		assertThat(librairies.get(0).getName()).isNotEmpty();
		assertThat(librairies.get(0).getId()).isNotNull();
	}

}
