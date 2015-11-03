package fr.simple.edm.common.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EdmDocumentSearchResultWrapperDto {
    
    // took time in MS
    private long tookTime;
    
    private long totalHitsCount;
    
    private List<EdmDocumentSearchResultDto> searchResults;
    
    public EdmDocumentSearchResultWrapperDto() {
        searchResults = new ArrayList<>();
    }
    
    public void add(EdmDocumentSearchResultDto edmDocumentSearchResultDto) {
        searchResults.add(edmDocumentSearchResultDto);
    }
}
