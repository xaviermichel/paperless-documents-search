package fr.simple.edm.mapper;

import javax.inject.Named;

import fr.simple.edm.common.dto.EdmSourceDto;
import fr.simple.edm.domain.EdmSource;

@Named
public class EdmSourceMapper extends AbstractMapper<EdmSource, EdmSourceDto> {

    public EdmSourceMapper() {
        super(EdmSource.class, EdmSourceDto.class);
    }

}
