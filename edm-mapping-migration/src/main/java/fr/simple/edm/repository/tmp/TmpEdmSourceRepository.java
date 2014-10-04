package fr.simple.edm.repository.tmp;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.tmp.TmpEdmSource;


public interface TmpEdmSourceRepository extends ElasticsearchRepository<TmpEdmSource, String> {

}
