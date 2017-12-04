package fr.simple.edm.repository;

import fr.simple.edm.domain.EdmCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface EdmCategoryRepository extends ElasticsearchRepository<EdmCategory, String> {

    List<EdmCategory> findByName(String name);

}
