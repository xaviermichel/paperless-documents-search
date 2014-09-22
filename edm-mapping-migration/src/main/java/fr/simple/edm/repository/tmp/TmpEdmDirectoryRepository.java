package fr.simple.edm.repository.tmp;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.tmp.TmpEdmDirectory;


public interface TmpEdmDirectoryRepository extends ElasticsearchRepository<TmpEdmDirectory, String> {

}
