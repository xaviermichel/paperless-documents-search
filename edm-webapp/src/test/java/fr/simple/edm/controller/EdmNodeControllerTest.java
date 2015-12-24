package fr.simple.edm.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.service.EdmNodeService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EdmNodeControllerTest {
	
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
	private EdmNodeController edmNodeController;
	
	private EdmNodeService edmNodeService;

	private EdmCategory edmCategory;
	
	private EdmDocumentFile edmDocumentFile1;
	
	private EdmDocumentFile edmDocumentFile2;
	
	@Before
    public void setup() {
		mockMvc = webAppContextSetup(webApplicationContext).build();
		
		edmNodeService = mock(EdmNodeService.class);    

		edmCategory = mock(EdmCategory.class);
		when(edmCategory.getId()).thenReturn("parent_0");
		
    	edmDocumentFile1 = new EdmDocumentFile();
    	edmDocumentFile1.setId("document_id_2");
    	edmDocumentFile1.setName("document_name_2");
    	edmDocumentFile1.setNodePath("documents/1");
    	edmDocumentFile1.setParentId(edmCategory.getId());
    	
    	edmDocumentFile2 = new EdmDocumentFile();
    	edmDocumentFile2.setId("document_id_2");
    	edmDocumentFile2.setName("document_name_2");
    	edmDocumentFile2.setNodePath("documents/2");
    	edmDocumentFile2.setParentId(edmCategory.getId());
    	
        when(edmNodeService.findOneByPath(edmDocumentFile1.getNodePath())).thenReturn(edmDocumentFile1);
        when(edmNodeService.findOne(edmDocumentFile1.getId())).thenReturn(edmDocumentFile1);
        when(edmNodeService.getChildren(edmCategory.getId())).thenReturn(Arrays.asList(edmDocumentFile1, edmDocumentFile2));
        
        edmNodeController.setEdmNodeService(edmNodeService);
    }
    
	@Test
	public void getNodeByIdShouldReturnTheNode() throws Exception {
    	mockMvc.perform(get("/node/" + edmDocumentFile1.getId()).accept(contentType))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id", is(edmDocumentFile1.getId())));
    	
        verify(edmNodeService, times(1)).findOne(edmDocumentFile1.getId());
        verifyNoMoreInteractions(edmNodeService);
	}
	
    @Test
    public void postNodeShouldCallNodeService() throws Exception {
		String documentJson = json(edmDocumentFile1);
    	
		mockMvc.perform(post("/node").contentType(contentType).content(documentJson))
			.andExpect(status().isOk());
        
        verify(edmNodeService, times(1)).save(edmDocumentFile1);
        verifyNoMoreInteractions(edmNodeService);
    }
    
    @Test
    public void deleteNodeShouldCallNodeService() throws Exception {  	
		mockMvc.perform(delete("/node/" + edmDocumentFile1.getId()).contentType(contentType))
			.andExpect(status().is2xxSuccessful());
        
        verify(edmNodeService, times(1)).deleteRecursively(edmDocumentFile1);
    }
	
    @Test
    public void getNodeByPathShouldExtractPathFromUrl() throws Exception {
    	mockMvc.perform(get("/node/path/" + edmDocumentFile1.getNodePath()).accept(contentType))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id", is(edmDocumentFile1.getId())));
    	
        verify(edmNodeService, times(1)).findOneByPath(edmDocumentFile1.getNodePath());
        verifyNoMoreInteractions(edmNodeService);
    }
    
    @Test
    public void getNodeChildsShouldReturnChildrensNode() throws Exception {
    	mockMvc.perform(get("/node/childs/" + edmCategory.getId()).accept(contentType))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$", hasSize(2)))
    		.andExpect(jsonPath("$[0].id", is(edmDocumentFile1.getId())))
    		.andExpect(jsonPath("$[1].id", is(edmDocumentFile2.getId())));
    	
        verify(edmNodeService, times(1)).getChildren(edmCategory.getId());
        verifyNoMoreInteractions(edmNodeService);
    }
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
