package fr.simple.edm.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EdmCategoryAggregationItem extends EdmAggregationItem {

	private EdmCategory category;

	@Builder
	private EdmCategoryAggregationItem(String key, long docCount, EdmCategory category) {
		super(key, docCount);
		this.category = category;
	}
}
