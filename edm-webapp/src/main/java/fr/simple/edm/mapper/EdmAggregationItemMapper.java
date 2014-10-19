package fr.simple.edm.mapper;

import javax.inject.Named;

import fr.simple.edm.common.dto.EdmAggregationItemDto;
import fr.simple.edm.model.EdmAggregationItem;

@Named
public class EdmAggregationItemMapper extends AbstractMapper<EdmAggregationItem, EdmAggregationItemDto> {

	public EdmAggregationItemMapper() {
		super(EdmAggregationItem.class, EdmAggregationItemDto.class);
	}

}
