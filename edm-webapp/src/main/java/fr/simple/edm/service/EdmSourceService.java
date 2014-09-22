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
	private EdmSourceRepository edmDirectoryRepository;
    
    public EdmSource findOne(String id) {
    	return edmDirectoryRepository.findOne(id);
    }
    
	public EdmSource save(EdmSource edmDirectory) {
        return edmDirectoryRepository.index(edmDirectory);
	}
	
	public List<EdmSource> findByParent(String parentId) {
	    Page<EdmSource> page = edmDirectoryRepository.findByParentId(parentId, new PageRequest(0, 99, new Sort(Sort.Direction.ASC, "name")));
		return page.getContent();
	}

	public List<EdmSource> findByName(String name) {
	    return edmDirectoryRepository.findByName(name);
	}
	
	public void delete(EdmSource edmDirectory) {
	    edmDirectoryRepository.delete(edmDirectory);
	}
}
