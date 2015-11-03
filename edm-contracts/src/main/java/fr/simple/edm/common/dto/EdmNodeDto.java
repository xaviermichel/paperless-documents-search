package fr.simple.edm.common.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import fr.simple.edm.common.EdmNodeType;

/**
 * Some edm node is something in the edm tree
 * @author xavier
 *
 */
@Data
@NoArgsConstructor
public class EdmNodeDto implements Serializable {

    private String id;

    private EdmNodeType edmNodeType = null;
    
    private String parentId = null;
    
    private String name = null;
    
    public EdmNodeDto(EdmNodeType edmNodeType) {
        this.edmNodeType = edmNodeType;
    }
}

