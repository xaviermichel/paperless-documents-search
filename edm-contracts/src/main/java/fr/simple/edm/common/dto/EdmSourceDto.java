package fr.simple.edm.common.dto;

import java.io.Serializable;

import fr.simple.edm.common.EdmNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class EdmSourceDto extends EdmNodeDto implements Serializable {

    private String description;

    public EdmSourceDto() {
        super(EdmNodeType.SOURCE);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
