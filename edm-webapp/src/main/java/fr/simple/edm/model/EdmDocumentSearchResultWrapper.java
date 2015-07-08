package fr.simple.edm.model;

import java.util.ArrayList;
import java.util.List;

public class EdmDocumentSearchResultWrapper {
    
    // took time in MS
    private long tookTime;
    
    private long totalHitsCount;
    
    private List<EdmDocumentSearchResult> searchResults;
    
    public EdmDocumentSearchResultWrapper() {
        searchResults = new ArrayList<>();
    }
    
    public void add(EdmDocumentSearchResult edmDocumentSearchResult) {
        searchResults.add(edmDocumentSearchResult);
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

    public List<EdmDocumentSearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<EdmDocumentSearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
