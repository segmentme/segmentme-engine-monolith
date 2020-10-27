package io.segmentme.core.db.domain.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context-schema")
public class ContextSchema extends DbObject {
    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

    @Indexed
    private String integrationPointKey;

    private String name;

    private String rawPayload;

    private Map<String, Object> nodeValues;

    private String hash;


    public String computeHash() {
        return DigestUtils.sha256Hex(inlinePath.entrySet().stream().map(it -> it.getKey() + ":" + it.getValue().getRootType() + "," + it.getValue().getSubType()).collect(Collectors.joining()));
    }

    @Data
    @AllArgsConstructor
    public static class InlineType {
        private SchemaNodeType rootType;
        private SchemaNodeType subType;

        public static InlineType of(SchemaNodeType rootType, SchemaNodeType subType) {
            return new InlineType(rootType, subType);
        }
    }

}
