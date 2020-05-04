package fr.simple.edm.service;

import java.util.List;

import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmDocumentFileDto;
import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.repository.EdmDocumentRepository;
import fr.simple.edm.repository.EdmSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
@Slf4j
public class EdmSourceService {

	private final EdmSourceRepository edmSourceRepository;

	private final EdmDocumentRepository edmDocumentRepository;

	private final Client elasticsearchClient;

	public EdmSourceService(EdmSourceRepository edmSourceRepository,
			EdmDocumentRepository edmDocumentRepository,
			Client elasticsearchClient) {
		this.edmSourceRepository = edmSourceRepository;
		this.edmDocumentRepository = edmDocumentRepository;
		this.elasticsearchClient = elasticsearchClient;
	}

	public EdmSource findOne(String id) {
		return edmSourceRepository.findById(id).get();
	}

	public EdmSource save(EdmSource edmSource) {
		return edmSourceRepository.index(edmSource);
	}

	public List<EdmSource> findByName(String name) {
		return edmSourceRepository.findByName(name);
	}

	public void delete(EdmSource edmSource) {
		edmSourceRepository.delete(edmSource);
	}

	public EdmSource findOneByName(String sourceName) {
		return edmSourceRepository.findByName(sourceName).stream()
				.findFirst()
				.orElse(new EdmSource());
	}

	public List<EdmSource> findAll() {
		return createStreamFromIterator(edmSourceRepository.findAll().iterator()).collect(toList());
	}

	public void markCrawlingStart(String sourceId) {
		int pageSize = 10;

		Pageable pageRequest = PageRequest.of(0, pageSize);
		Page<EdmDocumentFile> edmDocumentPage = edmDocumentRepository.findBySourceId(sourceId, pageRequest);
		while (edmDocumentPage.getSize() > 0) {
			log.debug("EdmDocumentFile, findBySourceId page {} on {}", edmDocumentPage.getNumber() + 1, edmDocumentPage.getTotalPages());

			for (EdmDocumentFile doc : edmDocumentPage.getContent()) {
				IndexRequest indexRequest = new IndexRequest(EdmDocumentFile.INDEX_NAME, EdmDocumentFile.TYPE_NAME, doc.getId());
				indexRequest.source("forRemoval", Boolean.TRUE);

				UpdateQuery updateQuery = new UpdateQueryBuilder()
						.withIndexName(EdmDocumentFile.INDEX_NAME)
						.withType(EdmDocumentFile.TYPE_NAME)
						.withId(doc.getId())
						.withClass(EdmDocumentFileDto.class)
						.withIndexRequest(indexRequest)
						.build();
				elasticsearchClient.update(updateQuery.getUpdateRequest().index(EdmDocumentFile.INDEX_NAME).type(EdmDocumentFile.TYPE_NAME).id(doc.getId())).actionGet();
			}

			if (!edmDocumentPage.hasNext()) {
				break;
			}
			pageRequest = edmDocumentPage.nextPageable();
			edmDocumentPage = edmDocumentRepository.findBySourceId(sourceId, pageRequest);
		}
	}

	public void markCrawlingEnd(String sourceId) {
		edmDocumentRepository.deleteBySourceIdAndForRemovalIsTrue(sourceId);
	}
}
