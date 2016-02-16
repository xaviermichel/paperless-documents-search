package fr.simple.edm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(indexName = "documents", type = "document_file")
public class EdmDocumentFile implements Serializable {

    private String id;
    
    private String name;
    
    private String sourceId;
    
    private String categoryId;
    
    private String fileExtension;

    private String fileContentType;
    
    @Transient
    private byte[] fileContent;
    
    private Date date;
    
    @NotNull
    private String nodePath = null;
}
