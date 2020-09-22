package io.segmentme.core.service.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
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
    public ContextValueHolder prepareContext(JsonNode rawContext, ContextSchema schema) {
        ContextValueHolder context = new ContextValueHolder();
        context.setValues(new HashMap<>());
        context.setSchema(schema);
        rawContext.fields().forEachRemaining(it -> buildValuesMap(it.getKey(), it.getValue(), schema.getRootNode().getSubNodes(), context.getValues()));
        return context;
    }

    private void buildValuesMap(String path, JsonNode value, List<SchemaNode> schemaNodes, Map<String, Object> values) {
        Optional<SchemaNode> schemaNode = Optional.ofNullable(schemaNodes).orElseGet(ArrayList::new).stream().filter(it -> it.getName().equalsIgnoreCase(path)).findFirst();
        getNodeValue(value, schemaNode.orElseGet(() -> new SchemaNode().setPath(path)
                .setType(SchemaNodeType.getPossibleSchemaNodeTypes(value.getNodeType()).get(0))), values);
    }


    private void getNodeValue(JsonNode value, SchemaNode schemaNode, Map<String, Object> values) {
        SchemaNodeType resolvedType = getPotentialSchemaNodeType(value, schemaNode.getType());
        switch (resolvedType) {
            case STRING, NUMBER, BOOLEAN, DATE -> values.put(schemaNode.getPath(), getComparableValue(value, resolvedType));
            case OBJECT -> value.fields().forEachRemaining(objectField -> collectObjectValues(schemaNode, values, objectField));
            case ARRAY -> resolveArrayItems(schemaNode.getPath(), schemaNode, value, values);
        }
    }

    private SchemaNodeType getPotentialSchemaNodeType(JsonNode value, SchemaNodeType schemaNode) {
        return Optional.ofNullable(schemaNode).orElseGet(() -> SchemaNodeType.getPossibleSchemaNodeTypes(value.getNodeType()).get(0));
    }

    private void resolveArrayItems(String path, SchemaNode schemaNode, JsonNode array, Map<String, Object> values) {
        List<JsonNode> arrayItems = IteratorUtils.toList(array.elements());
        Map<String, List<Object>> objects = new HashMap<>();
        arrayItems.forEach(element -> {
            SchemaNodeType subtype = getPotentialSchemaNodeType(element, schemaNode.getSubType());
            switch (subtype) {
                case STRING, NUMBER, BOOLEAN, DATE -> {
                    putValue(objects, path, getComparableValue(element, subtype));
                }
                case OBJECT -> {
                    Map<String, Object> objectValues = new HashMap<>();
                    element.fields().forEachRemaining(objectField -> collectObjectValues(schemaNode, objectValues, objectField));
                    objectValues.forEach((key, value) -> {
                        putValue(objects, key, value);
                    });
                }
            }
        });
        values.putAll(objects);
    }

    private void collectObjectValues(SchemaNode schemaNode, Map<String, Object> objectValues, Map.Entry<String, JsonNode> objectField) {
        buildValuesMap(Optional.ofNullable(schemaNode.getSubNodes()).map(subnodes -> objectField.getKey()).orElse(schemaNode.getPath() + "." + objectField.getKey()), objectField.getValue(), schemaNode.getSubNodes(), objectValues);
    }

    private void putValue(Map<String, List<Object>> objects, String key, Object value) {
        List<Object> collectedValues = objects.getOrDefault(key, new ArrayList<>());
        collectedValues.add(value);
        objects.putIfAbsent(key, collectedValues);
    }

    public Comparable<?> getComparableValue(JsonNode value, SchemaNodeType type) {
        return switch (type) {
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
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }


}
