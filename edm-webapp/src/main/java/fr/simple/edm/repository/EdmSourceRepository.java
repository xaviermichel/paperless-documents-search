package fr.simple.edm.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.domain.EdmSource;


public interface EdmSourceRepository extends ElasticsearchRepository<EdmSource, String> {
    
    List<EdmSource> findByName(String name);
    
}
