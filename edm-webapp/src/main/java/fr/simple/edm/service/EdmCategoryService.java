package fr.simple.edm.service;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.repository.EdmCategoryRepository;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

@Service
public class EdmCategoryService {

    @Inject
    @Setter
    private EdmCategoryRepository edmCategoryRepository;

    public EdmCategory findOne(String id) {
        return edmCategoryRepository.findOne(id);
    }

    public List<EdmCategory> findAll() {
        return createStreamFromIterator(edmCategoryRepository.findAll().iterator()).collect(toList());
    }

    public EdmCategory save(EdmCategory edmCategory) {
        return edmCategoryRepository.index(edmCategory);
    }

    public List<EdmCategory> findByName(String name) {
        return edmCategoryRepository.findByName(name);
    }

    public void delete(EdmCategory edmCategory) {
        edmCategoryRepository.delete(edmCategory);
    }

    public EdmCategory findOneByName(String sourceName) {
        return edmCategoryRepository.findByName(sourceName).stream()
                .findFirst()
                .orElse(new EdmCategory());
    }
}
