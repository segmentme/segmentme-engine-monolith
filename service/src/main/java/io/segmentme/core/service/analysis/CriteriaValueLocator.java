package io.segmentme.core.service.analysis;

import io.segmentme.core.service.exception.CriteriaValueLocatorException;
import io.segmentme.core.service.exception.error.CriteriaValueLocatorErrors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
@Slf4j
public class CriteriaValueLocator {
    private final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("(.*)\\[(\\d+)]", Pattern.MULTILINE);
    private final String ARRAY_INDEX_CLEANER = "\\[[0-9]+]";

    public Object getCriteriaValue(String path, ContextValueHolder context) {
        String clearPath = path.replaceAll(ARRAY_INDEX_CLEANER, StringUtils.EMPTY);

        Object o = context.getValues().get(clearPath);

        if (o == null && !context.getValues().containsKey(clearPath)) {
            throw new CriteriaValueLocatorException(clearPath, null, CriteriaValueLocatorErrors.CRITERIA_NOT_FOUND);
        }

        if (o instanceof List) {
            try {
                return getCollectionValue((List<?>) o, path, clearPath, null);
            } catch (Throwable ex) {
                log.warn("Unable to get value for criteria {} ", path);
                throw new CriteriaValueLocatorException(clearPath, ex, CriteriaValueLocatorErrors.UNEXPECTED_LOCATOR_ERROR);
            }
        }
        return o;
    }

    private List<?> getCollectionValue(List<?> collection, String path, String clearPath, Integer parentIndex) {
        boolean hasUnderlineCollections = collection.stream().anyMatch(it -> it instanceof Collection);
        MutablePair<String, Integer> currentPosition = getElementIndexIfPossible(path, clearPath);
        if (!hasUnderlineCollections) {
            Integer index = currentPosition == null ? parentIndex : currentPosition.getRight();
            if (index == null) {
                return collection;
            } else {
                if (collection.size() - 1 < index) {
                    return null;
                }
                return Arrays.asList(collection.get(index));
            }
        } else {
            String nextArray = currentPosition != null ? clearPath.replace(currentPosition.left, "") : clearPath;
            if (StringUtils.isNotBlank(nextArray) && !Objects.equals(nextArray, clearPath)) {
                if (collection.size() - 1 < currentPosition.getRight()) {
                    return Collections.emptyList();
                }
                return Stream.of(collection.get(currentPosition.getRight()))
                    .map(it -> getCollectionValue((List<?>) it, path, nextArray, null)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());

            }
            Integer currentIndex = currentPosition != null ? currentPosition.getRight() : null;
            return collection.stream().map(it -> getCollectionValue((List<?>) it, path, nextArray, currentIndex)).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        }

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

    public static String cleanPath(String path) {
        return path.replaceAll(ARRAY_INDEX_CLEANER, StringUtils.EMPTY);
    }
}
