package fr.simple.edm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Service;

import fr.simple.edm.model.EdmCategory;
import fr.simple.edm.repository.EdmCategoryRepository;

@Service
@PropertySources(value = { 
        @PropertySource("classpath:/edm-configuration.properties")
})
public class EdmCategoryService {

    @Inject
    private EdmCategoryRepository edmCategoryRepository;
    
    public EdmCategory findOne(String id) {
        return edmCategoryRepository.findOne(id);
    }

    public List<EdmCategory> getEdmLibraries() {
        List<EdmCategory> edmLibraries = new ArrayList<>();
        for (EdmCategory l : edmCategoryRepository.findAll()) {
            edmLibraries.add(l);
        }
        return edmLibraries;
    }

    public EdmCategory save(EdmCategory edmLibrary) {
        return edmCategoryRepository.index(edmLibrary);
    }

    public List<EdmCategory> findByName(String name) {
        return edmCategoryRepository.findByName(name);
    }
    
    public void delete(EdmCategory edmLibrary) {
        edmCategoryRepository.delete(edmLibrary);
    }
}
