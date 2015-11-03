package fr.simple.edm.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

import com.drew.lang.annotations.NotNull;

import fr.simple.edm.common.EdmNodeType;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
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
}
