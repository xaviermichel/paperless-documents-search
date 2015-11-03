package fr.simple.edm.common.dto;

import java.io.Serializable;
import java.util.Date;

import fr.simple.edm.common.EdmNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
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

}
