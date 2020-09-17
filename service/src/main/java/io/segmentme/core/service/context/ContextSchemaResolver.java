package io.segmentme.core.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import io.segmentme.core.service.utils.DateResolver;
import io.segmentme.core.service.workspace.UserConfigurationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    private final UserConfigurationService userConfigurationService;

    public static final String PATH_SPLITERATOR = ".";

    public static final String ROOT = "root";

    public ContextSchema resolve(JsonNode jsonNode) {
        return resolve(resolveSchemaNode(jsonNode));
    }

    public ContextSchema resolve(SchemaNode node) {
        ContextSchema contextSchema = new ContextSchema();
        contextSchema.setRootNode(node);
        contextSchema.setInlinePath(resolveInlinePath(contextSchema.getRootNode()));
        return contextSchema;
    }

    private SchemaNode resolveSchemaNode(JsonNode jsonNode) {
        SchemaNode root = new SchemaNode().setName(ROOT).setType(SchemaNodeType.OBJECT);
        root.setSubNodes(transformToSchemaNodes(jsonNode.fields(), it -> true));
        return root;
    }

    private Map<String, InlineType> resolveInlinePath(SchemaNode rootNode) {
        return resolveInlinePath(StringUtils.EMPTY, rootNode);
    }


    public Map<String, InlineType> resolveInlinePath(String path, SchemaNode node) {
        val inlinePath = new HashMap<String, InlineType>();

        String pathPrefix = StringUtils.isBlank(path) ? StringUtils.EMPTY : path + PATH_SPLITERATOR;

        if (!StringUtils.isBlank(path)) {
            inlinePath.put(path, InlineType.of(node.getType(), node.getSubType()));
            node.setPath(path);
        }

        Optional.ofNullable(node.getSubNodes())
                .ifPresent(subNodes -> subNodes.stream().map(it -> resolveInlinePath(pathPrefix + it.getName(), it)).forEach(inlinePath::putAll));

        return inlinePath;
    }


    private List<SchemaNode> transformToSchemaNodes(Iterator<Map.Entry<String, JsonNode>> nodes, Predicate<Map.Entry<String, JsonNode>> fiterNodes) {
        Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(nodes, 0), false);
        return stream.filter(fiterNodes).map(it -> convertToSchemaNode(it.getKey(), it.getValue())).collect(Collectors.toList());

    }

    private SchemaNode convertToSchemaNode(String name, JsonNode json) {
        SchemaNode schemaNode = new SchemaNode();
        schemaNode.setType(resolveNodeType(json));
        schemaNode.setName(name);

        if (schemaNode.getType() == SchemaNodeType.OBJECT) {
            schemaNode.setSubNodes(transformToSchemaNodes(json.fields(), it -> true));
        } else if (schemaNode.getType() == SchemaNodeType.ARRAY) {
            ArrayNodeDescriptor arrayNodeDescriptor = buildArrayDescriptor(schemaNode.getName(), json.elements());
            schemaNode.setSubNodes(arrayNodeDescriptor.getNodes());
            schemaNode.setSubType(arrayNodeDescriptor.getArraySubType());
        }
        return schemaNode;
    }

    private ArrayNodeDescriptor buildArrayDescriptor(String parent, Iterator<JsonNode> json) {
        ArrayNodeDescriptor arrayNodeDescriptor = new ArrayNodeDescriptor();
        List<SchemaNode> nodes = new ArrayList<>();
        List<String> updatedProperties = new ArrayList<>();
        json.forEachRemaining(arrayItem -> {
            SchemaNodeType type = resolveNodeType(arrayItem);
            if (arrayNodeDescriptor.getArraySubType() == null) {
                arrayNodeDescriptor.setArraySubType(type);
            } else if (arrayNodeDescriptor.getArraySubType() != type) {
                arrayNodeDescriptor.setArraySubType(SchemaNodeType.UNDEFINED);
            }

            if (type == SchemaNodeType.OBJECT) {
                List<SchemaNode> objectNodes = transformToSchemaNodes(arrayItem.fields(), it -> !updatedProperties.contains(parent + PATH_SPLITERATOR + it.getKey())).stream().peek(it -> updatedProperties.add(parent + PATH_SPLITERATOR + it.getName())).collect(Collectors.toList());
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

    private SchemaNodeType resolveNodeType(JsonNode json) {
        return switch (json.getNodeType()) {
            case ARRAY -> SchemaNodeType.ARRAY;
            case BOOLEAN -> SchemaNodeType.BOOLEAN;
            case MISSING, NULL, BINARY -> null;
            case NUMBER -> SchemaNodeType.NUMBER;
            case OBJECT, POJO -> SchemaNodeType.OBJECT;
            case STRING -> checkForDateType(json);
        };
    }

    private SchemaNodeType checkForDateType(JsonNode json) {
        String text = json.asText();
        if (text.length() > 50 || text.length() < 4) {
            return SchemaNodeType.STRING;
        }
        return DateResolver.resolve(json.textValue(), userConfigurationService.getDateFormats())
                .map(it -> SchemaNodeType.DATE).orElseGet(() -> SchemaNodeType.STRING);
    }
}
