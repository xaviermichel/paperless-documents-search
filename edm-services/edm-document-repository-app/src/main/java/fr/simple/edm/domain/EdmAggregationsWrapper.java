package fr.simple.edm.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmAggregationsWrapper {

	private List<EdmAggregationItem> aggregates = new ArrayList<>();

}
