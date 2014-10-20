package fr.simple.edm.mapper;

import javax.inject.Inject;
import javax.inject.Named;

import fr.simple.edm.common.dto.EdmDocumentSearchResultWrapperDto;
import fr.simple.edm.model.EdmDocumentSearchResult;
import fr.simple.edm.model.EdmDocumentSearchResultWrapper;

@Named
public class EdmDocumentSearchResultWrapperMapper extends AbstractMapper<EdmDocumentSearchResultWrapper, EdmDocumentSearchResultWrapperDto> {
    
    @Inject
    private EdmDocumentSearchResultMapper edmDocumentSearchResultMapper;
    
    public EdmDocumentSearchResultWrapperMapper() {
		super(EdmDocumentSearchResultWrapper.class, EdmDocumentSearchResultWrapperDto.class);
	}

    @Override
    public EdmDocumentSearchResultWrapperDto boToDto(EdmDocumentSearchResultWrapper bo) {
    	EdmDocumentSearchResultWrapperDto dto = new EdmDocumentSearchResultWrapperDto();
        
    	dto.setTookTime(bo.getTookTime());
    	dto.setTotalHitsCount(bo.getTotalHitsCount());
    	
    	for (EdmDocumentSearchResult boResult : bo.getSearchResults()) {
    		dto.add(edmDocumentSearchResultMapper.boToDto(boResult));
    	}
 
        return dto;
    }
}
	
