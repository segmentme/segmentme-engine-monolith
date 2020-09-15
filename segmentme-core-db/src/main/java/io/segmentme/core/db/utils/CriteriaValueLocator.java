package io.segmentme.core.db.utils;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.service.ContextHolder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class CriteriaValueLocator {
    private final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(.*)\\[(\\d+)\\]", Pattern.MULTILINE);
    private final String ARRAY_INDEX_CLEANER = "\\[[0-9]+\\]";

    public Object getCriteriaValue(String path, ContextHolder context) {
        String clearPath = path.replaceAll(ARRAY_INDEX_CLEANER, StringUtils.EMPTY);

        Object o = context.getValues().get(clearPath);

        if (o instanceof List) {
            try {
                return collectionValue((List<?>) o, path, clearPath, context.getSchema());
            } catch (Throwable ex) {
                log.warn("Unable to get value for criteria {} ", path);
                return null;
            }
        }
        return o;
    }

    private List<?> collectionValue(List<?> collection, String path, String clearPath, ContextSchema schema) {
        if (CollectionUtils.isEmpty(collection)) {
            return collection;
        }
        MutablePair<String, Integer> currentPosition = getElementIndexIfPossible(path, clearPath);

        boolean hasUnderlineCollections = collection.stream().anyMatch(it -> it instanceof Collection);

        if (!hasUnderlineCollections) {
            return Optional.ofNullable(currentPosition).map(it -> Collections.singletonList((Object) collection.get(currentPosition.getValue()))).orElse((List<Object>) collection);
        }

        if (currentPosition == null) {
            return collection.stream().map(it -> (List<?>) it)
                    .map(it -> collectionValue(it, path, clearPath, schema))
                    .flatMap(Collection::stream).collect(Collectors.toList());
        }

        return collectionValue(((List<?>) collection.get(currentPosition.getRight())), path, clearPath.substring(currentPosition.left.length()), schema);

    }

    private MutablePair<String, Integer> getElementIndexIfPossible(String path, String clearPath) {
        Matcher matcher = ARRAY_INDEX_PATTERN.matcher(path);

        MutablePair<String, Integer> currentPosition = null;

        if (matcher.find()) {
            String[] node = path.split("\\.");
            currentPosition = Arrays.stream(node)
                    .sequential()
                    .filter(it -> it.matches(ARRAY_INDEX_PATTERN.pattern()))
                    .map(ARRAY_INDEX_PATTERN::matcher)
                    .peek(Matcher::find)
                    .filter(it -> clearPath.contains(it.group(1)))
                    .findFirst()
                    .map(it -> new MutablePair<>(clearPath.substring(0, clearPath.lastIndexOf(it.group(1)) + it.group(1).length()), Integer.valueOf(it.group(2))))
                    .orElse(null);
        }
        return currentPosition;
    }
}
