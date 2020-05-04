package fr.simple.edm.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.service.EdmCategoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EdmCategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EdmCategoryService edmCategoryService;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private EdmCategory category1;

	private EdmCategory category2;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {
		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
				hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
		Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() {
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
		when(edmCategoryService.save(category1)).thenReturn(category1);
	}

	@Test
	public void findAllShouldReturnsAllCategories() throws Exception {
		mockMvc.perform(get("/categories").accept(contentType))
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

		mockMvc.perform(post("/categories").contentType(contentType).content(categoryJson))
				.andExpect(status().isOk());

		verify(edmCategoryService, times(1)).save(category1);
		verifyNoMoreInteractions(edmCategoryService);
	}

	@Test
	public void getCategoryByNameShouldCallCategoryService() throws Exception {
		mockMvc.perform(get("/categories?categoryName=" + category2.getName()).accept(contentType))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id", is(category2.getId())))
				.andExpect(jsonPath("$.name", is(category2.getName())))
				.andExpect(jsonPath("$.description", is(category2.getDescription())));

		verify(edmCategoryService, times(1)).findOneByName(category2.getName());
		verifyNoMoreInteractions(edmCategoryService);
	}


	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
