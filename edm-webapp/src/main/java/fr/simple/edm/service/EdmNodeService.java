package fr.simple.edm.service;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmNode;
import fr.simple.edm.domain.EdmSource;

@Service
public class EdmNodeService {

    @Inject
    private EdmCategoryService edmCategoryService;

    @Inject
    private EdmDocumentService edmDocumentService;

    @Inject
    private EdmSourceService edmSourceService;

    public EdmNode findOne(String nodeid) {
        return ObjectUtils.firstNonNull(edmCategoryService.findOne(nodeid), edmSourceService.findOne(nodeid), edmDocumentService.findOne(nodeid));
    }

    public EdmNode findOneByPath(String path) {
        //
        // not the best way, but may one of the fastest
        //

        // find the node name (last item)
        String[] fragmentedPath = path.split("/");
        ArrayUtils.reverse(fragmentedPath);
        String nodeName = fragmentedPath[0];

        // candidates have the right name
        List<EdmNode> candidates = ListUtils.union(ListUtils.union(edmCategoryService.findByName(nodeName), edmSourceService.findByName(nodeName)), edmDocumentService.findByName(nodeName));

        // the winner is the one which has the right path
        for (EdmNode node : candidates) {
            if (getPathOfNode(node).equalsIgnoreCase(path)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Delete node (and files) recursively starting at the given node
     */
    public void deleteRecursively(EdmNode node) {
        // now delete in database
        deleteNodeRecursively(node);
    }

    private void deleteNodeRecursively(EdmNode node) {
        for (EdmNode n : getChildren(node.getId())) {
            deleteNodeRecursively(n);
        }
        // one will succeed ... don't care about which one
        if (node instanceof EdmDocumentFile) {
            edmDocumentService.delete((EdmDocumentFile) node);
        } else if (node instanceof EdmSource) {
            edmSourceService.delete((EdmSource) node);
        } else if (node instanceof EdmCategory) {
            edmCategoryService.delete((EdmCategory) node);
        }
    }

    /**
     * get the relative path of the given node
     * 
     * @param edmNode
     *            The node you wan't to know the path
     * @return Relative path to this node
     */
    public String getPathOfNode(EdmNode edmNode) {
        String path = edmNode.getName();
        while (edmNode.getParentId() != null) {
            edmNode = findOne(edmNode.getParentId());
            path = edmNode.getName() + "/" + path;
        }
        return path;
    }
    
    public List<EdmNode> getChildren(String nodeid) {
        return ListUtils.union(edmDocumentService.findByParent(nodeid), edmSourceService.findByParent(nodeid));
    }

    public EdmNode save(EdmNode node) {
        EdmNode edmNode = findOne(node.getId());
        if (edmNode instanceof EdmDocumentFile) {
            edmNode = edmDocumentService.save((EdmDocumentFile) node);
        } else if (edmNode instanceof EdmSource) {
            edmNode = edmSourceService.save((EdmSource) node);
        } else if (edmNode instanceof EdmCategory) {
            edmNode = edmCategoryService.save((EdmCategory) node);
        }
        return edmNode;
    }
}
