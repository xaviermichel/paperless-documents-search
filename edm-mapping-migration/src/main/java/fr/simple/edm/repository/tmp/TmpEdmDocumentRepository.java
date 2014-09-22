package fr.simple.edm.repository.tmp;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import fr.simple.edm.model.tmp.TmpEdmDocument;


public interface TmpEdmDocumentRepository extends ElasticsearchRepository<TmpEdmDocument, String> {

}
