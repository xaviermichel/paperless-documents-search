package fr.simple.edm.repository;

import fr.simple.edm.domain.EdmSource;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface EdmSourceRepository extends ElasticsearchRepository<EdmSource, String> {

    List<EdmSource> findByName(String name);

}
