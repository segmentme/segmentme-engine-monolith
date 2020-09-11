package io.segmentme.core.db.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.segmentme.core.db.domain.context.AnalysisContextSchema.InlineType;
import static org.apache.commons.lang3.time.DateFormatUtils.*;

@UtilityClass
@Slf4j
public class AnalysisContextSchemaResolver {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'"),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'"),
            DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(SMTP_DATETIME_FORMAT.getPattern())
    );

    public static final String PATH_SPLITERATOR = ".";

    public AnalysisContextSchema resolve(JsonNode jsonNode) {
        AnalysisContextSchema contextSchema = new AnalysisContextSchema();
        Map<String, InlineType> paths = new HashMap<>();

        SchemaNode root = new SchemaNode().setName("root").setType(SchemaNodeType.OBJECT);
        contextSchema.setInlinePath(paths);

        Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.fields(), 0), false);

        root.setSubNodes(stream.map(it -> convertToSchemaNode(StringUtils.EMPTY, it.getKey(), it.getValue(), paths)).collect(Collectors.toList()));

        contextSchema.setRootNode(root);
        return contextSchema;
    }

    private SchemaNode convertToSchemaNode(String parent, String name, JsonNode json, Map<String, InlineType> paths) {
        SchemaNode schemaNode = new SchemaNode();
        schemaNode.setType(resolveNodeType(json));
        schemaNode.setName(name);

        String path = parent + name;


        if (schemaNode.getType() == SchemaNodeType.OBJECT) {
            Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(json.fields(), 0), false);
            schemaNode.setSubNodes(stream.map(it -> convertToSchemaNode(path + PATH_SPLITERATOR, it.getKey(), it.getValue(), paths)).collect(Collectors.toList()));
        } else if (schemaNode.getType() == SchemaNodeType.ARRAY) {
            ArrayNodeDescriptor arrayNodeDescriptor = resolveArrayElements(path, json.elements());
            schemaNode.setSubNodes(arrayNodeDescriptor.getNodes());
            schemaNode.setSubType(arrayNodeDescriptor.getArraySubType());
            paths.putAll(arrayNodeDescriptor.getPaths());
        }
        paths.put(path, InlineType.of(schemaNode.getType(), schemaNode.getSubType()));
        return schemaNode;
    }

    private ArrayNodeDescriptor resolveArrayElements(String parent, Iterator<JsonNode> json) {
        ArrayNodeDescriptor arrayNodeDescriptor = new ArrayNodeDescriptor();
        List<SchemaNode> nodes = new ArrayList<>();
        arrayNodeDescriptor.setNodes(nodes);
        json.forEachRemaining(arrayItem -> {
            SchemaNodeType type = resolveNodeType(arrayItem);
            arrayNodeDescriptor.setArraySubType(type);
            if (type == SchemaNodeType.OBJECT) {
                Stream<Map.Entry<String, JsonNode>> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(arrayItem.fields(), 0), false);
                nodes.addAll(stream.filter(it -> !arrayNodeDescriptor.getPaths().containsKey(parent + "." + it.getKey())).map(it -> convertToSchemaNode(parent + ".", it.getKey(), it.getValue(), arrayNodeDescriptor.getPaths())).collect(Collectors.toList()));
            }

        });

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

    private static SchemaNodeType checkForDateType(JsonNode json) {
        String text = json.asText();
        if (text.length() > 50) {
            return SchemaNodeType.STRING;
        }
        return DATE_TIME_FORMATTERS.stream().map(it -> {
            try {
                return it.parse(text);
            } catch (Throwable ex) {
                log.debug("Unable to parse {} to format {}", text, it);
                return null;
            }
        }).filter(Objects::nonNull).findAny().map(it -> SchemaNodeType.DATE).orElseGet(() -> SchemaNodeType.STRING);
    }
}
