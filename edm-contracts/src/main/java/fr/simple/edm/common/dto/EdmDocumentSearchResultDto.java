package fr.simple.edm.common.dto;


public class EdmDocumentSearchResultDto {

    private EdmDocumentFileDto edmDocument;
    
    private String highlightedName;
    
    private String highlightedDescription;
    
    private String highlightedFileContentMatching;
    
    private String highlightedNodePath;
    
    public EdmDocumentFileDto getEdmDocument() {
        return edmDocument;
    }

    public void setEdmDocument(EdmDocumentFileDto edmDocument) {
        this.edmDocument = edmDocument;
    }

    public String getHighlightedName() {
        return highlightedName;
    }

    public void setHighlightedName(String highlightedName) {
        this.highlightedName = highlightedName;
    }

    public String getHighlightedDescription() {
        return highlightedDescription;
    }

    public void setHighlightedDescription(String highlightedDescription) {
        this.highlightedDescription = highlightedDescription;
    }

    public String getHighlightedFileContentMatching() {
        return highlightedFileContentMatching;
    }

    public void setHighlightedFileContentMatching(String highlightedFileContentMatching) {
        this.highlightedFileContentMatching = highlightedFileContentMatching;
    }

    public String getHighlightedNodePath() {
        return highlightedNodePath;
    }

    public void setHighlightedNodePath(String highlightedNodePath) {
        this.highlightedNodePath = highlightedNodePath;
    }
}
