package fr.simple.edm.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;

import fr.simple.edm.common.EdmNodeType;

/**
 * Some edm node is something in the edm tree
 * @author xavier
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@MappedSuperclass
public class EdmNode implements Serializable, Comparable<EdmNode> {

    @Id
    private String id;
    
    @NotNull
    private EdmNodeType edmNodeType;
    
    private String parentId = null;
    
    @NotNull
    private String name;
    
    public EdmNode(EdmNodeType edmNodeType) {
        this.edmNodeType = edmNodeType;
    }
    
    @Override
    public int compareTo(EdmNode other) {
        return getName().compareTo(other.getName());
    }
}
