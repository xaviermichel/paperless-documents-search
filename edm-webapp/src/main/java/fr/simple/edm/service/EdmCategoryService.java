package fr.simple.edm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.repository.EdmCategoryRepository;
import lombok.Setter;

@Service
public class EdmCategoryService {

    @Inject
    @Setter
    private EdmCategoryRepository edmCategoryRepository;

    public EdmCategory findOne(String id) {
        return edmCategoryRepository.findOne(id);
    }

    public List<EdmCategory> findAll() {
        List<EdmCategory> edmLibraries = new ArrayList<>();
        edmCategoryRepository.findAll().forEach(edmLibraries::add);
        return edmLibraries;
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
