package io.segmentme.db.config.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ReferenceFieldeCallback implements ReflectionUtils.FieldCallback {

    private boolean idFound;

    @Override
    public void doWith(final Field field) {
        ReflectionUtils.makeAccessible(field);

        if (field.isAnnotationPresent(Id.class)) {
            idFound = true;
        }
    }

    public boolean isIdFound() {
        return idFound;
    }
}
