package fr.simple.edm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.EdmDocumentFile;


public interface EdmDocumentRepository extends ElasticsearchRepository<EdmDocumentFile, String> {

    @Query("{\"match\": {\"parentId\" : \"?0\"}}")
    Page<EdmDocumentFile> findByParentId(String parentId, Pageable page);

	List<EdmDocumentFile> findByName(String name);
	
	EdmDocumentFile findByUniqueIdentifier(String uniqueIdentifier);
}
