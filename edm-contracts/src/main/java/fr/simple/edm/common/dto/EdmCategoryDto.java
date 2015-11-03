package fr.simple.edm.common.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import fr.simple.edm.common.EdmNodeType;

@AllArgsConstructor
@Builder
@Data
public class EdmCategoryDto extends EdmNodeDto implements Serializable {

    private String description;

    public EdmCategoryDto() {
        super(EdmNodeType.CATEGORY);
    }
}
