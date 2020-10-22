package io.segmentme.core.service.analysis;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.service.exception.CriteriaValueLocatorException;
import io.segmentme.core.service.exception.error.CriteriaValueLocatorErrors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class ContextValueHolder {
    private Map<String, Object> values;

    private ContextSchema schema;

    private Map<String, Object> extractedValues = new HashMap<>();


    public <T> T getValue(String criteria, Class<T> result) {
        Object o = getValue(criteria);
        if (o instanceof Throwable) {
            throw (CriteriaValueLocatorException) o;
        } else {
            if (result.isAssignableFrom(Collection.class)) {
                return (T) o;
            } else {
                return (T) castIfRequired(o, criteria);
            }
        }
    }

    public Object getValue(String criteria) {
        return extractedValues.computeIfAbsent(criteria, it -> {
            try {

                return CriteriaValueLocator.getCriteriaValue(it, this);

            } catch (CriteriaValueLocatorException ex) {
                return ex;
            } catch (Throwable ex) {
                return new CriteriaValueLocatorException(criteria, null, CriteriaValueLocatorErrors.UNEXPECTED_LOCATOR_ERROR);
            }
        });
    }


    @SuppressWarnings("unchecked")
    private Comparable<Object> castIfRequired(Object propertyValue, String propertyName) {
        if (!(propertyValue instanceof Collection<?>)) {
            return (Comparable<Object>) propertyValue;
        }

        var collectionProperty = (Collection<Comparable<Object>>) propertyValue;

        if (CollectionUtils.size(collectionProperty) > 1) {
            log.warn("Property {} is a collections with size {}", propertyName, CollectionUtils.size(collectionProperty));
            throw new CriteriaValueLocatorException(propertyName, null, CriteriaValueLocatorErrors.UNEXPECTED_ARRAY_TYPE);
        }

        return collectionProperty.stream().findFirst().orElseThrow(() -> new RuntimeException("Collection property is empty"));
    }

}
