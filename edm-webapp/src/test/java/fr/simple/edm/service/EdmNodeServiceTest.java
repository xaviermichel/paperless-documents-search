package fr.simple.edm.service;

import static org.fest.assertions.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fr.simple.edm.Application;
import fr.simple.edm.ElasticsearchTestingHelper;
import fr.simple.edm.common.EdmNodeType;
import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmNode;
import fr.simple.edm.domain.EdmSource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
public class EdmNodeServiceTest {

    @Autowired
    private EdmNodeService edmNodeService;

    @Autowired
    private EdmCategoryService edmCategoryService;

    @Autowired
    private EdmDocumentService edmDocumentService;

    @Autowired
    private EdmSourceService edmSourceService;

    @Autowired
    private ElasticsearchTestingHelper elasticsearchTestingHelper;

    // testing id's
    private EdmCategory edmLibrary;
    private String libraryId;

    private EdmSource edmDirectory;
    private String directoryId;

    private EdmDocumentFile edmDocument;
    private String documentId;

    private EdmSource directoryWithDirectoryParent;
    private String directoryWithDirectoryParentId;

    private EdmDocumentFile documentUnderLibrary;
    private String documentUnderLibraryId;

    /*
     * Faked library =============
     * 
     * % gedLibrary 
     *      + gedDirectory 
     *          - gedDocument 
     *          + directoryWithDirectoryParent
     *      - documentUnderLibrary
     */

    @Before
    public void setUp() throws Exception {
        
        String targetDirAbsolutePath = System.getProperty("user.dir") + (System.getProperty("user.dir").contains("edm-webapp") ? "" : "/edm-webapp") + "/target/test-classes/";
        
        elasticsearchTestingHelper.destroyAndRebuildIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);

        // building a fake environment
        edmLibrary = new EdmCategory();
        edmLibrary.setName("library");
        edmLibrary = edmCategoryService.save(edmLibrary);

        libraryId = edmLibrary.getId();

        edmDirectory = new EdmSource();
        edmDirectory.setName("directory");
        edmDirectory.setParentId(edmLibrary.getId());
        edmDirectory = edmSourceService.save(edmDirectory);

        directoryId = edmDirectory.getId();

        edmDocument = new EdmDocumentFile();
        edmDocument.setName("document");
        edmDocument.setParentId(edmDirectory.getId());
        // make a copy because moving test file is not acceptable (someone may come after and require this file) ! 
        Files.copy(Paths.get(targetDirAbsolutePath + "demo_pdf.pdf"), Paths.get(targetDirAbsolutePath + "demo_pdf_tmp.pdf"));
        edmDocument.setFilename(targetDirAbsolutePath + "demo_pdf_tmp.pdf");
        edmDocument.setNodePath("/documents/1");
        
        edmDocument = edmDocumentService.save(edmDocument);

        documentId = edmDocument.getId();

        directoryWithDirectoryParent = new EdmSource();
        directoryWithDirectoryParent.setName("subdirectory");
        directoryWithDirectoryParent.setParentId(edmDirectory.getId());
        directoryWithDirectoryParent = edmSourceService.save(directoryWithDirectoryParent);

        directoryWithDirectoryParentId = directoryWithDirectoryParent.getId();

        documentUnderLibrary = new EdmDocumentFile();
        documentUnderLibrary.setName("document under library");
        documentUnderLibrary.setParentId(edmLibrary.getId());
        documentUnderLibrary = edmDocumentService.save(documentUnderLibrary);
        documentUnderLibrary.setNodePath("/documents/2");

        documentUnderLibraryId = documentUnderLibrary.getId();

        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }

    @Test
    public void libraryNodeShouldBeReturned() {
        EdmNode node = edmNodeService.findOne(libraryId);
        assertThat(node).isNotNull();
        assertThat(node.getId()).isEqualTo(libraryId);
        assertThat(node.getEdmNodeType()).isEqualTo(EdmNodeType.CATEGORY);
    }

    @Test
    public void directoryNodeShouldBeReturned() {
        EdmNode node = edmNodeService.findOne(directoryId);
        assertThat(node).isNotNull();
        assertThat(node.getId()).isEqualTo(directoryId);
        assertThat(node.getEdmNodeType()).isEqualTo(EdmNodeType.SOURCE);
    }

    @Test
    public void documentNodeShouldBeReturned() {
        EdmNode node = edmNodeService.findOne(documentId);
        assertThat(node).isNotNull();
        assertThat(node.getId()).isEqualTo(documentId);
        assertThat(node.getEdmNodeType()).isEqualTo(EdmNodeType.DOCUMENT);
    }

    @Test
    public void libraryShouldNotHaveParent() {
        EdmNode node = edmNodeService.findOne(libraryId);
        assertThat(node.getParentId()).isNull();
    }

    @Test
    public void directoryCanHaveLibrayForParent() {
        EdmNode node = edmNodeService.findOne(directoryId);
        assertThat(node.getParentId()).isEqualTo(libraryId);
    }

    @Test
    public void directoryCanHaveDirectoryForParent() {
        EdmNode node = edmNodeService.findOne(directoryWithDirectoryParentId);
        assertThat(node.getParentId()).isEqualTo(directoryId);
    }

    @Test
    public void documentCanHaveLibraryForParent() {
        EdmNode node = edmNodeService.findOne(documentUnderLibraryId);
        assertThat(node.getParentId()).isEqualTo(libraryId);
    }

    @Test
    public void documentCanHaveDirectoryForParent() {
        EdmNode node = edmNodeService.findOne(documentId);
        assertThat(node.getParentId()).isEqualTo(directoryId);
    }

    @Test
    public void libraryPathShouldBeLibraryName() {
        EdmNode node = edmNodeService.findOne(libraryId);
        String nodePath = edmNodeService.getPathOfNode(node);
        assertThat(nodePath).isEqualTo("library");
    }

    @Test
    public void directoryPathShouldIncludeLibraryPath() {
        EdmNode node = edmNodeService.findOne(directoryId);
        String nodePath = edmNodeService.getPathOfNode(node);
        assertThat(nodePath).isEqualTo("library/directory");
    }

    @Test
    public void documentPathShouldIncludeDirectoryPath() {
        EdmNode node = edmNodeService.findOne(documentId);
        String nodePath = edmNodeService.getPathOfNode(node);
        assertThat(nodePath).isEqualTo("library/directory/document");
    }

    @Test
    public void libraryHasExpectedChildren() {
        List<EdmNode> nodes = edmNodeService.getChildren(libraryId);

        List<EdmNode> attemptedResult = Arrays.asList(new EdmNode[] { edmDirectory, documentUnderLibrary });

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(attemptedResult.size());
        assertThat(nodes).containsAll(attemptedResult);
    }

    @Test
    public void directoryHasExpectedChildren() {
        List<EdmNode> nodes = edmNodeService.getChildren(directoryId);

        List<EdmNode> attemptedResult = Arrays.asList(new EdmNode[] { directoryWithDirectoryParent, edmDocument });

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(attemptedResult.size());
        assertThat(nodes).containsAll(attemptedResult);
    }

    @Test
    public void anotherDirectoryHasExpectedChildren() {
        List<EdmNode> nodes = edmNodeService.getChildren(directoryWithDirectoryParentId);

        List<EdmNode> attemptedResult = Arrays.asList(new EdmNode[] {});

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(attemptedResult.size());
        assertThat(nodes).containsAll(attemptedResult);
    }

    @Test
    public void documentShouldNotHaveChildren() {
        List<EdmNode> nodes = edmNodeService.getChildren(documentId);

        List<EdmNode> attemptedResult = Arrays.asList(new EdmNode[] {});

        assertThat(nodes).isNotNull();
        assertThat(nodes.size()).isEqualTo(attemptedResult.size());
        assertThat(nodes).containsAll(attemptedResult);
    }

    @Test
    public void libraryShouldBeFindByPath() {
        EdmNode node = edmNodeService.findOneByPath("library");
        
        assertThat(node).isNotNull();
        assertThat(node).isEqualTo(edmLibrary);
    }
    
    @Test
    public void directoryShouldBeFindByPath() {
        EdmNode node = edmNodeService.findOneByPath("library/directory");
        
        assertThat(node).isNotNull();
        assertThat(node).isEqualTo(edmDirectory);
    }
    
    @Test
    public void documentShouldBeFindByPath() {
        EdmNode node = edmNodeService.findOneByPath("library/directory/document");
        
        assertThat(node).isNotNull();
        assertThat(node).isEqualTo(edmDocument);
    }
    
    @Test
    public void subdirectoryShouldBeFindByPath() {
        EdmNode node = edmNodeService.findOneByPath("library/directory/subdirectory");
        
        assertThat(node).isNotNull();
        assertThat(node).isEqualTo(directoryWithDirectoryParent);
    }
    
    @Test
    public void nodeIdStartingWithDashIsAccepted() {
        EdmSource newDirectory = new EdmSource();
        newDirectory.setName("new directory");
        newDirectory.setParentId(libraryId);
        newDirectory.setId("-azerty");
        newDirectory = edmSourceService.save(newDirectory);
        
        EdmNode node = edmNodeService.findOne(newDirectory.getId());
        assertThat(node).isNotNull();
        assertThat(node).isEqualTo(newDirectory);
                
        List<EdmNode> nodes = edmNodeService.getChildren(newDirectory.getId());
        assertThat(nodes).isEmpty();
    }
   
}
