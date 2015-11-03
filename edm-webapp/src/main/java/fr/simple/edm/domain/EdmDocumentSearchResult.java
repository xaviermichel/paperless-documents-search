package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search result
 * 
 * @author xavier
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmDocumentSearchResult {

    private EdmDocumentFile edmDocument;
    
    private String highlightedName;
    
    private String highlightedDescription;
    
    private String highlightedFileContentMatching;
    
    private String highlightedNodePath;
    
}
