package fr.simple.edm.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EdmCategoryAggregationItemDto extends EdmAggregationItemDto {

	private EdmCategoryDto category;

	@Builder
	private EdmCategoryAggregationItemDto(String key, long docCount, EdmCategoryDto category) {
		super(key, docCount);
		this.category = category;
	}
}
