package io.segmentme.core.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public abstract class AbstractDatabaseService<E, R extends MongoRepository<E, String>> {
    @Autowired
    protected R repository;

    public Optional<E> findById(String id) {
        return repository.findById(id);
    }

    public E create(E entity) {
        return repository.save(entity);
    }


}
