package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EdmAggregationItem {
    
    private String key;
    
    private long docCount;
    
}
