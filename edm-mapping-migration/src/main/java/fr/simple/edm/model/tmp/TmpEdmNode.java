package fr.simple.edm.model.tmp;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.Id;

import fr.simple.edm.common.EdmNodeType;

/**
 * Some edm node is something in the edm tree
 * 
 * @author xavier
 * 
 */
@MappedSuperclass
public class TmpEdmNode implements Serializable {

    @Id
    private String id;

    private EdmNodeType edmNodeType;

    private String parentId = null;

    private String name;

    private String nodePath;

    public TmpEdmNode(EdmNodeType edmNodeType) {
        this.edmNodeType = edmNodeType;
    }

    public TmpEdmNode() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EdmNodeType getEdmNodeType() {
        return edmNodeType;
    }

    public void setEdmNodeType(EdmNodeType edmNodeType) {
        this.edmNodeType = edmNodeType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((edmNodeType == null) ? 0 : edmNodeType.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TmpEdmNode other = (TmpEdmNode) obj;
        if (edmNodeType != other.edmNodeType) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
