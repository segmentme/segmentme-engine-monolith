package io.segmentme.core.db.domain.context;

import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum SchemaNodeType {
    OBJECT(Arrays.asList(JsonNodeType.OBJECT, JsonNodeType.POJO)),
    ARRAY(Collections.singletonList(JsonNodeType.ARRAY)),
    STRING(Collections.singletonList(JsonNodeType.STRING)),
    DATE(Collections.singletonList(JsonNodeType.STRING)),
    NUMBER(Collections.singletonList(JsonNodeType.NUMBER)),
    BOOLEAN(Collections.singletonList(JsonNodeType.BOOLEAN)),
    UNDEFINED(Arrays.asList(JsonNodeType.NULL, JsonNodeType.BINARY));
    private final List<JsonNodeType> jsonNodeType;

    public static List<SchemaNodeType> getPossibleSchemaNodeTypes(JsonNodeType jsonNodeType) {
        return Arrays.stream(SchemaNodeType.values()).filter(it -> it.jsonNodeType != null)
            .filter(it -> it.jsonNodeType.contains(jsonNodeType)).collect(Collectors.toList());
    }
}
