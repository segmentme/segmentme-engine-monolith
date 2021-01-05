package io.segmentme.core.db.config.mongo;

import lombok.SneakyThrows;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

public class CascadeCallback implements ReflectionUtils.FieldCallback {

    private final Object source;
    private final MongoOperations mongoOperations;

    CascadeCallback(final Object source, final MongoOperations mongoOperations) {
        this.source = source;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(final Field field) {
        ReflectionUtils.makeAccessible(field);
        Optional.of(field)
            .filter(it -> it.isAnnotationPresent(DBRef.class))
            .filter(it -> it.isAnnotationPresent(CascadeSave.class))
            .map(this::getSource)
            .ifPresent(this::saveObject);
    }

    private void saveObject(Object fieldValue) {
        if (fieldValue instanceof Collection<?>) {
            ((Collection<?>) fieldValue).forEach(this::save);
        } else {
            save(fieldValue);
        }
    }

    private void save(Object fieldValue) {
        final ReferenceFieldeCallback callback = new ReferenceFieldeCallback();

        ReflectionUtils.doWithFields(fieldValue.getClass(), callback);

        this.mongoOperations.save(fieldValue);
    }

    @SneakyThrows
    private Object getSource(final Field field) {
        return field.get(source);
    }
}
