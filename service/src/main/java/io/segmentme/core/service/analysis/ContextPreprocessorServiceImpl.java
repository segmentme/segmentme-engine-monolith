package io.segmentme.core.service.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.db.service.ContextHolder;
import io.segmentme.core.service.utils.DateResolver;
import io.segmentme.core.service.workspace.UserConfigurationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ContextPreprocessorServiceImpl implements ContextPreprocessorService {

    private final UserConfigurationServiceImpl userConfigurationService;

    @Override
    public ContextHolder prepareContext(JsonNode rawContext, ContextSchema schema) {
        ContextHolder context = new ContextHolder();
        context.setValues(new HashMap<>());
        context.setSchema(schema);
        rawContext.fields().forEachRemaining(it -> buildValuesMap(it.getKey(), it.getValue(), schema.getRootNode().getSubNodes(), context.getValues()));
        return context;
    }

    private void buildValuesMap(String key, JsonNode value, List<SchemaNode> schemaNodes, Map<String, Object> values) {
        Optional<SchemaNode> schemaNode = schemaNodes.stream().filter(it -> it.getName().equalsIgnoreCase(key)).findFirst();
        schemaNode.ifPresent(it -> getKnownValues(value, it, values));
    }

    private void getKnownValues(JsonNode value, SchemaNode schemaNode, Map<String, Object> values) {
        getKnownValues(value, schemaNode, schemaNode.getPath(), values);
    }

    private void getKnownValues(JsonNode value, SchemaNode schemaNode, String path, Map<String, Object> values) {
        getKnownValues(value, schemaNode.getType(), schemaNode, path, values);
    }

    private void getKnownValues(JsonNode value, SchemaNodeType type, SchemaNode schemaNode, String path, Map<String, Object> values) {
        switch (type) {
            case STRING, NUMBER, BOOLEAN, DATE -> values.put(path, getSingularValue(value, type));
            case OBJECT -> value.fields().forEachRemaining(it -> buildValuesMap(it.getKey(), it.getValue(), schemaNode.getSubNodes(), values));
            case ARRAY -> resolveArrayItems(schemaNode, value, values);
        }
    }

    private void resolveArrayItems(SchemaNode schemaNode, JsonNode context, Map<String, Object> values) {
        SchemaNodeType subType = schemaNode.getSubType();
        List<JsonNode> arrayItems = IteratorUtils.toList(context.elements());
        Map<String, List<Object>> objects = new HashMap<>();
        arrayItems.forEach(element -> {
            switch (subType) {
                case STRING, NUMBER, BOOLEAN, DATE -> {
                    putValue(objects, schemaNode.getPath(), getSingularValue(element, subType));
                }
                case OBJECT -> {
                    Map<String, Object> objectOverview = new HashMap<>();
                    element.fields().forEachRemaining(it -> buildValuesMap(it.getKey(), it.getValue(), schemaNode.getSubNodes(), objectOverview));

                    objectOverview.forEach((key, value) -> {
                        putValue(objects, key, value);
                    });
                }
            }
        });
        values.putAll(objects);
    }

    private void putValue(Map<String, List<Object>> objects, String key, Object value) {
        List<Object> collectedValues = objects.getOrDefault(key, new ArrayList<>());
        collectedValues.add(value);
        objects.putIfAbsent(key, collectedValues);
    }

    public Comparable<?> getSingularValue(JsonNode value, SchemaNodeType type) {
        return switch (type) {
            case OBJECT, UNDEFINED, ARRAY -> null;
            case STRING -> value.asText();
            case NUMBER -> value.numberValue().doubleValue();
            case BOOLEAN -> value.booleanValue();
            case DATE -> {
                Optional<Instant> resolve = DateResolver.resolve(value.asText(), userConfigurationService.getDateFormats());
                if (resolve.isPresent()) {
                    yield resolve.get();
                } else {
                    yield value.asText();
                }
            }
        };

    }

}
