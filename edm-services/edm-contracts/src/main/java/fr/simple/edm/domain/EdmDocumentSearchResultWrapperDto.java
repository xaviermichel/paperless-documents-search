package fr.simple.edm.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmDocumentSearchResultWrapperDto {

	// took time in MS
	private long tookTime;

	private long totalHitsCount;

	private List<EdmDocumentSearchResultDto> searchResults;

}
