package fr.simple.edm.model;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;

import com.drew.lang.annotations.NotNull;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "documents", type = "document_file", shards = 1, replicas = 0)
public class EdmDocumentFile extends EdmNode {

    private String filename;

    private String fileExtension;

    private String fileContentType;

    private Date date;

    @NotNull
    private String nodePath;
    
    public EdmDocumentFile() {
        super(EdmNodeType.DOCUMENT);
        filename = "";
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}
