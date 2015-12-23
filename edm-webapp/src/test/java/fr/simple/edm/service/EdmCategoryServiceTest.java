package fr.simple.edm.service;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.simple.edm.Application;
import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.repository.EdmCategoryRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmCategoryServiceTest {

	private EdmCategoryService sut;
	
	private EdmCategory category1;
	
	private EdmCategory category2;
	
	
	@Before
    public void setup() {
		sut = new EdmCategoryService();
		
    	category1 = new EdmCategory();
    	category1.setId("category_id_1");
    	category1.setName("category_name_1");
    	category1.setDescription("category_description_1");
    	
    	category2 = new EdmCategory();
    	category2.setId("category_id_2");
    	category2.setName("category_name_2");
    	category2.setDescription("category_description_2");
	}
	
	@Test
	public void getAllCategoryShouldReturnsAllCategories() {
		// given
		EdmCategoryRepository edmCategoryRepository = mock(EdmCategoryRepository.class);
		sut.setEdmCategoryRepository(edmCategoryRepository);
		when(edmCategoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));
		
		// when
		List<EdmCategory> categories = sut.findAll();
		
		// then
		assertThat(categories).isNotNull();
		assertThat(categories.size()).isEqualTo(2);
		assertThat(categories).contains(category1, category2);
	}
	
	@Test
	public void returnEmptyObjectIfCategoryNameNotMatch() {
		// given
		EdmCategoryRepository edmCategoryRepository = mock(EdmCategoryRepository.class);
		sut.setEdmCategoryRepository(edmCategoryRepository);
		when(edmCategoryRepository.findByName("unkown_category")).thenReturn(new ArrayList<>());
		
		// when
		EdmCategory edmCategory = sut.findOneByName("unkown_category");
		
		// then
		assertThat(edmCategory).isNotNull();
		assertThat(edmCategory.getId()).isNull();
	}
	
	@Test
	public void deleteShouldCallDao() {
		// given
		EdmCategoryRepository edmCategoryRepository = mock(EdmCategoryRepository.class);
		sut.setEdmCategoryRepository(edmCategoryRepository);
		
		// when
		sut.delete(category1);
		
		// then
        verify(edmCategoryRepository, times(1)).delete(any(EdmCategory.class));
        verifyNoMoreInteractions(edmCategoryRepository);
	}
}
