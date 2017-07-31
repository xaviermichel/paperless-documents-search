package fr.simple.edm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.repository.EdmDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class EdmCrawlingService {

    @Inject
    private EdmDocumentService edmDocumentService;

    @Inject
    private EdmDocumentRepository edmDocumentRepository;

    @Inject
    private EdmSourceService edmSourceService;


    // Map<source, List<documentId>>, is used to delete removed document at re-indexation
    private static Map<String, List<String>> sourceDocumentsIds;

    static {
        sourceDocumentsIds = new HashMap<>();
    }

    public EdmDocumentFile save(EdmDocumentFile edmDocument) {
        edmDocument = edmDocumentService.save(edmDocument);
        if (sourceDocumentsIds.get(edmDocument.getSourceId()) != null) {
            sourceDocumentsIds.get(edmDocument.getSourceId()).remove(edmDocument.getId());
        } else {
            log.warn("Indexing document without source (have you hit /crawl/start first ?");
        }
        return edmDocument;
    }

    public void snapshotCurrentDocumentsForSource(String sourceName) {

        EdmSource source = edmSourceService.findOneByName(sourceName);
        if (StringUtils.isEmpty(source.getId())) {
            return;
        }
        String sourceId = source.getId();

        List<String> edmDocumentsIds = new ArrayList<>();

        int pageSize = 10;

        Pageable pageRequest = new PageRequest(0, pageSize);
        Page<EdmDocumentFile> edmDocumentPage = edmDocumentRepository.findBySourceId(sourceId, pageRequest);

        while (edmDocumentPage.getSize() > 0) {
            log.debug("EdmDocumentFile, findBySourceId page {} on {}", edmDocumentPage.getNumber() + 1, edmDocumentPage.getTotalPages());

            for (EdmDocumentFile doc : edmDocumentPage.getContent()) {
                edmDocumentsIds.add(doc.getId());
            }

            if (!edmDocumentPage.hasNext()) {
                break;
            }
            pageRequest = edmDocumentPage.nextPageable();
            edmDocumentPage = edmDocumentRepository.findBySourceId(sourceId, pageRequest);
        }
        sourceDocumentsIds.put(sourceId, edmDocumentsIds);

        log.info("The snapshot contains {} documents for source {}", edmDocumentsIds.size(), source);
    }

    public void deleteUnusedDocumentsBeforeSnapshotForSource(String sourceName) {
        EdmSource source = edmSourceService.findOneByName(sourceName);
        if (source == null) {
            return;
        }
        String sourceId = source.getId();

        if (sourceDocumentsIds.get(sourceId) == null) {
            return;
        }

        log.info("Will delete {} unused document(s) for source '{}'", sourceDocumentsIds.get(sourceId).size(), sourceId);
        // loop on removed documents
        for (String documentId : sourceDocumentsIds.get(sourceId)) {
            EdmDocumentFile edmDocumentFile = edmDocumentRepository.findOne(documentId);
            log.debug("Delete document : {} ({})", edmDocumentFile.getNodePath(), edmDocumentFile.getId());
            edmDocumentRepository.delete(edmDocumentFile);
        }
        // reset map to be sur new ids won't be deleted
        sourceDocumentsIds.put(sourceId, new ArrayList<String>());
    }
}
