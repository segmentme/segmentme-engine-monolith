package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.DbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractDatabaseService<E extends DbObject, R extends MongoRepository<E, String>> {
    @Autowired
    protected R repository;

    public Optional<E> findById(String id) {
        return repository.findById(id);
    }

    public E create(E entity) {
        return repository.save(entity);
    }

    public E update(E update) {
        return repository.save(update);
    }

    public Collection<E> updateAll(Collection<E> update) {
        return repository.saveAll(update);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void delete(E entity) {
        repository.delete(entity);
    }

    public void deleteAll(Collection<E> entity) {
        repository.deleteAll(entity);
    }

}
