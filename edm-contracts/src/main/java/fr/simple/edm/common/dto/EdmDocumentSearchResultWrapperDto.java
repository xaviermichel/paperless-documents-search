package fr.simple.edm.common.dto;

import java.util.ArrayList;
import java.util.List;


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
    
    public long getTookTime() {
        return tookTime;
    }

    public void setTookTime(long tookTime) {
        this.tookTime = tookTime;
    }

    public long getTotalHitsCount() {
        return totalHitsCount;
    }

    public void setTotalHitsCount(long totalHitsCount) {
        this.totalHitsCount = totalHitsCount;
    }

    public List<EdmDocumentSearchResultDto> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<EdmDocumentSearchResultDto> searchResults) {
        this.searchResults = searchResults;
    }
}
