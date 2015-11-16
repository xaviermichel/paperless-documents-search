package fr.simple.edm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Document(indexName = "documents", type = "document_file")
public class EdmDocumentFile extends EdmNode implements Serializable {

    private String filename;
    
    private String fileExtension;

    private String fileContentType;
    
    // only used to make a bridge with uploaded file
    @Transient
    private String temporaryFileToken;
    
    private Date date;
    
    @NotNull
    private String nodePath = null;
    
    public EdmDocumentFile() {
        super(EdmNodeType.DOCUMENT);
    }

}
