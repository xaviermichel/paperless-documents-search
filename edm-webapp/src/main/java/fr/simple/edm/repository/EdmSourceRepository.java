package fr.simple.edm.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.domain.EdmSource;


public interface EdmSourceRepository extends ElasticsearchRepository<EdmSource, String> {

    @Query("{\"match\": {\"parentId\" : \"?0\"}}")
    Page<EdmSource> findByParentId(String parentId, Pageable page);
    
    List<EdmSource> findByName(String name);
    
}
