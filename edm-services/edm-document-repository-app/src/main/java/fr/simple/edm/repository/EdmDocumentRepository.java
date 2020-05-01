package fr.simple.edm.repository;

import java.util.List;

import fr.simple.edm.domain.EdmDocumentFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface EdmDocumentRepository extends ElasticsearchRepository<EdmDocumentFile, String> {

	@Query("{\"match\": {\"sourceId\" : \"?0\"}}")
	Page<EdmDocumentFile> findBySourceId(String sourceId, Pageable page);

	List<EdmDocumentFile> findByName(String name);

	void deleteBySourceIdAndForRemovalIsTrue(String sourceId);
}
