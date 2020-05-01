package fr.simple.edm.repository;

import java.util.List;

import fr.simple.edm.domain.EdmSource;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EdmSourceRepository extends ElasticsearchRepository<EdmSource, String> {

	List<EdmSource> findByName(String name);

}
