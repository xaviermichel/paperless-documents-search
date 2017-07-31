package fr.simple.edm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.data.util.StreamUtils;
import org.springframework.stereotype.Service;

import fr.simple.edm.domain.EdmSource;
import fr.simple.edm.repository.EdmSourceRepository;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.util.StreamUtils.createStreamFromIterator;

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
}
