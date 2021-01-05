package io.segmentme.core.db.config.mongo;

import io.segmentme.core.db.domain.context.DbObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class BackReferenceCallback implements ReflectionUtils.FieldCallback {

    private final DbObject source;

    private final MongoOperations mongoOperations;

    BackReferenceCallback(final Object source, final MongoOperations mongoOperations) {
        this.source = (DbObject) source;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void doWith(final Field field) {
        ReflectionUtils.makeAccessible(field);
        Optional.of(field)
            .filter(it -> it.isAnnotationPresent(DBRef.class))
            .filter(it -> it.isAnnotationPresent(CascadeSave.class))
            .ifPresent(it -> updateIfRequired(this.getSource(it), it.getName()));
    }

    private void updateIfRequired(Object fieldValue, String name) {
        if (fieldValue instanceof Collection<?>) {
            ((Collection<?>) fieldValue).forEach(it -> update(it, name));
        } else {
            update(fieldValue, name);
        }
    }

    private void update(Object fieldValue, String name) {
        if (fieldValue == null) {
            return;
        }
        Arrays.stream(FieldUtils.getAllFields(fieldValue.getClass()))
            .collect(Collectors.toList())
            .stream()
            .filter(it -> it.isAnnotationPresent(BackReferenceId.class))
            .filter(it -> it.getAnnotation(BackReferenceId.class).value().equals(name))
            .map(it -> setValue(source.getId(), fieldValue, it))
            .findFirst()
            .ifPresent(it -> saveObject(fieldValue));
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
    private Field setValue(Object sourceId, Object fieldValue, Field field) {
        ReflectionUtils.makeAccessible(field);
        FieldUtils.writeField(field, fieldValue, sourceId);
        return field;
    }

    @SneakyThrows
    private Object getSource(final Field field) {
        return field.get(source);
    }
}
