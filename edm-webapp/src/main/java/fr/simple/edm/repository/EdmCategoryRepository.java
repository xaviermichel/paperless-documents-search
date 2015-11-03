package fr.simple.edm.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.domain.EdmCategory;


public interface EdmCategoryRepository extends ElasticsearchRepository<EdmCategory, String> {

    List<EdmCategory> findByName(String name);
    
}
