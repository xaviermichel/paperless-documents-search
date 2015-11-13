package fr.simple.edm.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the response which is sent from server when you upload some file. 
 * It give you the file server reference. It's an help for async uploads
 * 
 * @author xavier
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdmDocumentUploadResponse implements Serializable {

    private String temporaryFileToken;

}
