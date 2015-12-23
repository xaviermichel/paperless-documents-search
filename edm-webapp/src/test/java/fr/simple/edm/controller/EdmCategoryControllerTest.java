package fr.simple.edm.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import fr.simple.edm.Application;
import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.service.EdmCategoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EdmCategoryControllerTest {

	private MockMvc mockMvc;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	
	@Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
        Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Autowired
	private EdmCategoryController edmCategoryController;
	
	private EdmCategoryService edmCategoryService;
	
	private EdmCategory category1;
	
	private EdmCategory category2;
	
	@Before
    public void setup() {
		mockMvc = webAppContextSetup(webApplicationContext).build();
		
    	edmCategoryService = mock(EdmCategoryService.class);    
    	
    	category1 = new EdmCategory();
    	category1.setId("category_id_1");
    	category1.setName("category_name_1");
    	category1.setDescription("category_description_1");
    	
    	category2 = new EdmCategory();
    	category2.setId("category_id_2");
    	category2.setName("category_name_2");
    	category2.setDescription("category_description_2");

        when(edmCategoryService.findAll()).thenReturn(Arrays.asList(category1, category2));
        when(edmCategoryService.findOneByName(category1.getName())).thenReturn(category1);
        when(edmCategoryService.findOneByName(category2.getName())).thenReturn(category2);
        
        edmCategoryController.setEdmCategoryService(edmCategoryService);
    }
	
    @Test
    public void findAllShouldReturnsAllCategories() throws Exception {
    	mockMvc.perform(get("/category").accept(contentType))
        		.andExpect(status().isOk())
        		.andExpect(content().contentType("application/json;charset=UTF-8"))
        		.andExpect(jsonPath("$", hasSize(2)))
        		.andExpect(jsonPath("$[0].id", is(category1.getId())))
        		.andExpect(jsonPath("$[1].id", is(category2.getId())));
        
        verify(edmCategoryService, times(1)).findAll();
        verifyNoMoreInteractions(edmCategoryService);
    }
    
    
    @Test
    public void postCategoryShouldCallCategoryService() throws Exception {
    	 String categoryJson = json(category1);
    	
    	mockMvc.perform(post("/category").contentType(contentType).content(categoryJson))
    		.andExpect(status().isOk());
        
        verify(edmCategoryService, times(1)).save(any(EdmCategory.class));
        verifyNoMoreInteractions(edmCategoryService);
    }
    
    @Test
    public void getCategoryByNameShouldCallCategoryService() throws Exception {
    	mockMvc.perform(get("/category/name/" + category2.getName()).accept(contentType))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id", is(category2.getId())))
			.andExpect(jsonPath("$.name", is(category2.getName())))
			.andExpect(jsonPath("$.description", is(category2.getDescription())));
    	
        verify(edmCategoryService, times(1)).findOneByName(any(String.class));
        verifyNoMoreInteractions(edmCategoryService);
    }
    
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
