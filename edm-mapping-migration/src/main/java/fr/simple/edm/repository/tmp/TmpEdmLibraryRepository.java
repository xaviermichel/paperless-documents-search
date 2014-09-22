package fr.simple.edm.repository.tmp;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.tmp.TmpEdmLibrary;


public interface TmpEdmLibraryRepository extends ElasticsearchRepository<TmpEdmLibrary, String> {

}
