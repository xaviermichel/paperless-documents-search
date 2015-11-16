package fr.simple.edm.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Some edm node is something in the edm tree
 * 
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

    private String id;

    private EdmNodeType edmNodeType = null;

    private String parentId = null;

    private String name = null;

    public EdmNode(EdmNodeType edmNodeType) {
        this.edmNodeType = edmNodeType;
    }

    @Override
    public int compareTo(EdmNode other) {
        return getName().compareTo(other.getName());
    }
}
