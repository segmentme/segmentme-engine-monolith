package io.segmentme.core.db.domain.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context-schema")
public class AnalysisContextSchema extends DbObject {
    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

    @Data
    public static class InlineType {
        private SchemaNodeType rootType;
        private SchemaNodeType subType;

        public static final InlineType of(SchemaNodeType rootType, SchemaNodeType subType) {
            return new InlineType().setRootType(rootType).setSubType(subType);
        }
    }
}
