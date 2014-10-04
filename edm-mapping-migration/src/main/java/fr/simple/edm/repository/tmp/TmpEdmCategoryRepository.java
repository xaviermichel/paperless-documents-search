package fr.simple.edm.repository.tmp;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.tmp.TmpEdmCategory;


public interface TmpEdmCategoryRepository extends ElasticsearchRepository<TmpEdmCategory, String> {

}
