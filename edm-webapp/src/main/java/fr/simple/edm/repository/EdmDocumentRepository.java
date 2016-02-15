package fr.simple.edm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.domain.EdmDocumentFile;


public interface EdmDocumentRepository extends ElasticsearchRepository<EdmDocumentFile, String> {

    @Query("{\"match\": {\"sourceId\" : \"?0\"}}")
    Page<EdmDocumentFile> findBySourceId(String parentId, Pageable page);
    
    List<EdmDocumentFile> findByName(String name);

}
