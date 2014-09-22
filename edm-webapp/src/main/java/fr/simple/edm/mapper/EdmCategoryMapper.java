package fr.simple.edm.mapper;

import javax.inject.Named;

import fr.simple.edm.common.dto.EdmCategoryDto;
import fr.simple.edm.model.EdmCategory;

@Named
public class EdmCategoryMapper extends AbstractMapper<EdmCategory, EdmCategoryDto> {

	public EdmCategoryMapper() {
		super(EdmCategory.class, EdmCategoryDto.class);
	}

}
	
