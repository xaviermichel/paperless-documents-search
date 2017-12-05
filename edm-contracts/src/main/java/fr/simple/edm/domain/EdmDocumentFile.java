package fr.simple.edm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import fr.simple.edm.annotation.EdmSearchable;
import org.springframework.data.annotation.Transient;
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
@Document(indexName = "document_file", type = "document_file")
public class EdmDocumentFile implements Serializable {

    private String id;

    private String sourceId;

    private String categoryId;

    @EdmSearchable
    private String name;

    @Transient
    private byte[] binaryFileContent;

    @EdmSearchable
    private String fileContent;

    private String fileExtension;

    private String fileContentType;

    @EdmSearchable
    private String fileTitle;

    @EdmSearchable
    private String fileAuthor;

    @EdmSearchable
    private String fileKeywords;

    private Date date;

    @NotNull
    @EdmSearchable
    private String nodePath = null;
}
