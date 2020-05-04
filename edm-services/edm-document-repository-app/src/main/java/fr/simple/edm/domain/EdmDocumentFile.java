package fr.simple.edm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.simple.edm.annotation.EdmSearchable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(indexName = EdmDocumentFile.INDEX_NAME, type = EdmDocumentFile.TYPE_NAME)
public class EdmDocumentFile implements Serializable {

	public static final String INDEX_NAME = "document_file";

	public static final String TYPE_NAME = "document_file";

	private String id;

	private String sourceId;

	private String categoryId;

	private Boolean forRemoval;

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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date fileDate;

	@NotNull
	@EdmSearchable
	private String nodePath = null;
}
