package fr.simple.edm.mapper;

import javax.inject.Named;

import fr.simple.edm.common.dto.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmDocumentFile;

@Named
public class EdmDocumentMapper extends AbstractMapper<EdmDocumentFile, EdmDocumentFileDto> {

    public EdmDocumentMapper() {
        super(EdmDocumentFile.class, EdmDocumentFileDto.class);
    }

    @Override
    public EdmDocumentFileDto boToDto(EdmDocumentFile bo) {
        EdmDocumentFileDto dto = super.boToDto(bo);
        // additional fields
        if (dto.getId() != null && ! dto.getId().isEmpty()) {
            // actually, the storage strategy is very simple
            dto.setServerDocumentFilePath(bo.getNodePath() + "." + bo.getFileExtension());
        }
        return dto;
    }

}
