package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdmAggregationItem {

	private String key;

	private long docCount;

}
