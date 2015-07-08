package fr.simple.edm.common.dto;

import java.io.Serializable;

import fr.simple.edm.common.EdmNodeType;

public class EdmCategoryDto extends EdmNodeDto implements Serializable {

    private String description;

    public EdmCategoryDto() {
        super(EdmNodeType.CATEGORY);
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
