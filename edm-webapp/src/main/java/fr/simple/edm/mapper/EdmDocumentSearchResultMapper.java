package fr.simple.edm.mapper;

import javax.inject.Inject;
import javax.inject.Named;

import fr.simple.edm.common.dto.EdmDocumentSearchResultDto;
import fr.simple.edm.model.EdmDocumentSearchResult;

@Named
public class EdmDocumentSearchResultMapper extends AbstractMapper<EdmDocumentSearchResult, EdmDocumentSearchResultDto> {
    
    @Inject
    private EdmDocumentMapper edmDocumentMapper;
    
    public EdmDocumentSearchResultMapper() {
		super(EdmDocumentSearchResult.class, EdmDocumentSearchResultDto.class);
	}

    @Override
    public EdmDocumentSearchResultDto boToDto(EdmDocumentSearchResult bo) {
        EdmDocumentSearchResultDto dto = new EdmDocumentSearchResultDto();
        
        dto.setEdmDocument(edmDocumentMapper.boToDto(bo.getEdmDocument()));
        
        // TODO : automatic field value copy (excepted edmDocument)
        dto.setHighlightedDescription(bo.getHighlightedDescription());
        dto.setHighlightedFileContentMatching(bo.getHighlightedFileContentMatching());
        dto.setHighlightedName(bo.getHighlightedName());
        dto.setHighlightedNodePath(bo.getHighlightedNodePath());
        
        return dto;
    }
}
	
