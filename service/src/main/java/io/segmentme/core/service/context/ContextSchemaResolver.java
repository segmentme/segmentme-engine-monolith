package io.segmentme.core.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.service.utils.DateResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.segmentme.core.db.domain.context.ContextSchema.InlineType;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContextSchemaResolver {

    public static final String PATH_SPLITERATOR = ".";

    public static final String ROOT = "root";

    public ContextSchema resolve(Workspace workspace, JsonNode jsonNode) {
        return resolve(resolveSchemaNode(workspace, jsonNode));
    }

    public ContextSchema resolve(SchemaNode node) {
        ContextSchema contextSchema = new ContextSchema();
        contextSchema.setRootNode(node);
        contextSchema.setInlinePath(resolveInlinePath(contextSchema.getRootNode()));
        return contextSchema;
    }

    private SchemaNode resolveSchemaNode(Workspace workspace, JsonNode jsonNode) {
        List<DateTimeFormatter> dateFormats = workspace.getConfiguration()
                .toDateFormatters(workspace.getConfiguration().getKnownDateFormats());


        SchemaNode root = new SchemaNode().setName(ROOT).setType(SchemaNodeType.OBJECT);
        root.setSubNodes(transformToSchemaNodes(dateFormats, jsonNode.fields(), it -> true));
        return root;
    }

    private Map<String, InlineType> resolveInlinePath(SchemaNode rootNode) {
        return resolveInlinePath(StringUtils.EMPTY, rootNode);
    }


    private Map<String, InlineType> resolveInlinePath(String path, SchemaNode node) {
        var inlinePath = new HashMap<String, InlineType>();

        String pathPrefix = StringUtils.isBlank(path) ? StringUtils.EMPTY : path + PATH_SPLITERATOR;

        if (!StringUtils.isBlank(path)) {
            inlinePath.put(path, InlineType.of(node.getType(), node.getSubType()));
            node.setPath(path);
        }

        Optional.ofNullable(node.getSubNodes())
                .ifPresent(subNodes -> subNodes.stream().map(it -> resolveInlinePath(pathPrefix + it.getName(), it)).forEach(inlinePath::putAll));

        return inlinePath;
    }


    private List<SchemaNode> transformToSchemaNodes(List<DateTimeFormatter> dateFormats, Iterator<Map.Entry<String, JsonNode>> nodes, Predicate<Map.Entry<String, JsonNode>> fiterNodes) {
        Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodes, 0), false);
        return stream.filter(fiterNodes).map(it -> convertToSchemaNode(dateFormats, it.getKey(), it.getValue())).collect(Collectors.toList());

    }

    private SchemaNode convertToSchemaNode(List<DateTimeFormatter> dateFormats, String name, JsonNode json) {
        SchemaNode schemaNode = new SchemaNode();
        schemaNode.setType(resolveNodeType(json, dateFormats));
        schemaNode.setName(name);

        if (schemaNode.getType() == SchemaNodeType.OBJECT) {
            schemaNode.setSubNodes(transformToSchemaNodes(dateFormats, json.fields(), it -> true));
        } else if (schemaNode.getType() == SchemaNodeType.ARRAY) {
            ArrayNodeDescriptor arrayNodeDescriptor = buildArrayDescriptor(dateFormats, schemaNode.getName(), json.elements());
            schemaNode.setSubNodes(arrayNodeDescriptor.getNodes());
            schemaNode.setSubType(arrayNodeDescriptor.getArraySubType());
        }
        return schemaNode;
    }

    private ArrayNodeDescriptor buildArrayDescriptor(List<DateTimeFormatter> dateFormats, String parent, Iterator<JsonNode> json) {
        ArrayNodeDescriptor arrayNodeDescriptor = new ArrayNodeDescriptor();
        List<SchemaNode> nodes = new ArrayList<>();
        List<String> updatedProperties = new ArrayList<>();
        json.forEachRemaining(arrayItem -> {
            SchemaNodeType type = resolveNodeType(arrayItem, dateFormats);
            if (arrayNodeDescriptor.getArraySubType() == null) {
                arrayNodeDescriptor.setArraySubType(type);
            } else if (arrayNodeDescriptor.getArraySubType() != type) {
                arrayNodeDescriptor.setArraySubType(SchemaNodeType.UNDEFINED);
            }

            if (type == SchemaNodeType.OBJECT) {
                List<SchemaNode> objectNodes = transformToSchemaNodes(dateFormats, arrayItem.fields(), it -> !updatedProperties.contains(parent + PATH_SPLITERATOR + it.getKey())).stream().peek(it -> updatedProperties.add(parent + PATH_SPLITERATOR + it.getName())).collect(Collectors.toList());
                nodes.addAll(objectNodes);
            }
        });

        if (!CollectionUtils.isEmpty(nodes)) {
            arrayNodeDescriptor.setNodes(nodes);
        }
        return arrayNodeDescriptor;
    }

    @Data
    private static class ArrayNodeDescriptor {
        private List<SchemaNode> nodes;
        private SchemaNodeType arraySubType;
        Map<String, InlineType> paths = new HashMap<>();
    }

    private SchemaNodeType resolveNodeType(JsonNode json, List<DateTimeFormatter> dateTimeFormatters) {
        return switch (json.getNodeType()) {
            case ARRAY -> SchemaNodeType.ARRAY;
            case BOOLEAN -> SchemaNodeType.BOOLEAN;
            case MISSING, NULL, BINARY -> null;
            case NUMBER -> SchemaNodeType.NUMBER;
            case OBJECT, POJO -> SchemaNodeType.OBJECT;
            case STRING -> checkForDateType(json, dateTimeFormatters);
        };
    }

    private SchemaNodeType checkForDateType(JsonNode json, List<DateTimeFormatter> dateTimeFormatters) {
        String text = json.asText();
        if (text.length() > 50 || text.length() < 4) {
            return SchemaNodeType.STRING;
        }
        return DateResolver.resolve(json.textValue(), dateTimeFormatters)
                .map(it -> SchemaNodeType.DATE).orElseGet(() -> SchemaNodeType.STRING);
    }
}
