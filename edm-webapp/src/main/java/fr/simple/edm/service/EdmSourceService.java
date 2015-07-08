package fr.simple.edm.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.simple.edm.model.EdmSource;
import fr.simple.edm.repository.EdmSourceRepository;

@Service
public class EdmSourceService {

    @Inject
    private EdmSourceRepository edmSourceRepository;

    public EdmSource findOne(String id) {
        return edmSourceRepository.findOne(id);
    }

    public EdmSource save(EdmSource edmSource) {
        return edmSourceRepository.index(edmSource);
    }

    public List<EdmSource> findByParent(String parentId) {
        Page<EdmSource> page = edmSourceRepository.findByParentId(parentId, new PageRequest(0, 99, new Sort(Sort.Direction.ASC, "name")));
        return page.getContent();
    }

    public List<EdmSource> findByName(String name) {
        return edmSourceRepository.findByName(name);
    }

    public void delete(EdmSource edmSource) {
        edmSourceRepository.delete(edmSource);
    }

    public EdmSource findOneByName(String sourceName) {
        List<EdmSource> candidates = edmSourceRepository.findByName(sourceName);
        return candidates.isEmpty() ? new EdmSource() : candidates.get(0);
    }
}
