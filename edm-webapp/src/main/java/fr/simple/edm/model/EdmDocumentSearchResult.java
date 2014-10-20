package fr.simple.edm.model;

/**
 * Search result
 * 
 * @author xavier
 *
 */
public class EdmDocumentSearchResult {

    private EdmDocumentFile edmDocument;
    
    private String highlightedName;
    
    private String highlightedDescription;
    
    private String highlightedFileContentMatching;
    
    private String highlightedNodePath;
    
    public EdmDocumentFile getEdmDocument() {
        return edmDocument;
    }

    public void setEdmDocument(EdmDocumentFile edmDocument) {
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
