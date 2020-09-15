package io.segmentme.core.db.service.condition;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.service.ContextHolder;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class CriteriaValueLocator {

    public Object getCriteriaValue(String value, ContextHolder context) {
        Object o = context.getValues().get(value);

        if (o instanceof Collection) {
            return collectionValue((Collection) o, context.getSchema());
        }
        return o;
    }

    private Collection<Object> collectionValue(Collection<Object> collection, ContextSchema schema) {
        if (CollectionUtils.isEmpty(collection)) {
            return collection;
        }
        boolean isCollectionOfCollections = collection.stream().anyMatch(it -> it instanceof Collection);

        if (!isCollectionOfCollections) {
            return collection;
        }

        return collection.stream().map(it -> (Collection<Object>) it)
                .map(it -> collectionValue(it, schema))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }
}
