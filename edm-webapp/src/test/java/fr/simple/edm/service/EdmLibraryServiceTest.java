package fr.simple.edm.service;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.simple.edm.Application;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.domain.EdmCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
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
        elasticsearchTestingHelper.deleteAllDocumentsForIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
        
        EdmCategory category = new EdmCategory();
        category.setName("My category");
        edmCategory = edmCategoryService.save(category);
        
        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }
    
    /**
     * Leave the database after test
     */
    @After
    public void tearDown() throws Exception {
        elasticsearchTestingHelper.deleteAllDocumentsForIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }
    
    @Test
    public void categoryCanBeFindByHisName() {
        EdmCategory node = edmCategoryService.findOneByName("My category");
        assertThat(node).isNotNull();
        assertThat(node.getId()).isEqualTo(edmCategory.getId());
    }
}
