package fr.simple.edm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EdmAggregationItemDto {
	
	private String key;
	
	private long docCount;
	
}
