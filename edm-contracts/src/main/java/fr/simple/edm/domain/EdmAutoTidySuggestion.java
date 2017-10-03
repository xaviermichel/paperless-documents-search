package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EdmAutoTidySuggestion {

    private String suggestedFileLocation;

    private String suggestedFileName;

    private String suggestedExtension;

    // may be used for debug purpose
    private String originalNodePath;
}
