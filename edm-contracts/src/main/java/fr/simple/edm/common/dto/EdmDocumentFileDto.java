package fr.simple.edm.common.dto;

import java.io.Serializable;
import java.util.Date;

import fr.simple.edm.common.EdmNodeType;

public class EdmDocumentFileDto extends EdmNodeDto implements Serializable {

    private String fileExtension;

    private String fileContentType;
    
    private String serverDocumentFilePath;
    
    // only used to make a bridge with uploaded file
    private String temporaryFileToken;
    
    private Date date;
    
    private String nodePath = null;
    
    public EdmDocumentFileDto() {
        super(EdmNodeType.DOCUMENT);
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

    public String getTemporaryFileToken() {
        return temporaryFileToken;
    }

    public void setTemporaryFileToken(String temporaryFileToken) {
        this.temporaryFileToken = temporaryFileToken;
    }

    public String getServerDocumentFilePath() {
        return serverDocumentFilePath;
    }

    public void setServerDocumentFilePath(String serverDocumentFilePath) {
        this.serverDocumentFilePath = serverDocumentFilePath;
    }
    
    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}
