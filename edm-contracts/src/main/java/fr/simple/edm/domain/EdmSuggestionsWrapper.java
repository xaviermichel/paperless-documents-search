package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmSuggestionsWrapper {

    private List<EdmDocumentFile> documents = new ArrayList<>();

}
