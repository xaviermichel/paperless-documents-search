package fr.simple.edm.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmDocumentFileDto implements Serializable {

	private String id;

	private String sourceId;

	private String categoryId;

	private Boolean forRemoval;

	private String name;

	private byte[] binaryFileContent;

	private String fileContent;

	private String fileExtension;

	private String fileContentType;

	private String fileTitle;

	private String fileAuthor;

	private String fileKeywords;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date fileDate;

	@NotNull
	private String nodePath = null;
}
