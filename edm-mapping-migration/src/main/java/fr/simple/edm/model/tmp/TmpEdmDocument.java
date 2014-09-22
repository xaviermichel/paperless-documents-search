package fr.simple.edm.model.tmp;

import java.util.Date;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "tmp_documents", type = "tmp_document", shards = 1, replicas = 0)
public class TmpEdmDocument extends TmpEdmNode {

	private String description;

	private String filename; 
	
	private String fileExtension;
	
	private String fileContentType;
	
	private String fileContentMatchingHighlight;
	
	private Date date;
	
	
	public TmpEdmDocument() {
		super(EdmNodeType.DOCUMENT);
		filename = "";
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

    public String getFileContentMatchingHighlight() {
        return fileContentMatchingHighlight;
    }

    public void setFileContentMatchingHighlight(String fileContentMatchingHighlight) {
        this.fileContentMatchingHighlight = fileContentMatchingHighlight;
    }
}
