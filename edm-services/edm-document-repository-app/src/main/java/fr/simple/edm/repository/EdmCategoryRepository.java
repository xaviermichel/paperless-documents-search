package fr.simple.edm.repository;

import java.util.List;

import fr.simple.edm.domain.EdmCategory;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EdmCategoryRepository extends ElasticsearchRepository<EdmCategory, String> {

	List<EdmCategory> findByName(String name);

}
