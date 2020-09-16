package io.segmentme.core.db.domain.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context-schema")
public class ContextSchema extends DbObject {
    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

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
