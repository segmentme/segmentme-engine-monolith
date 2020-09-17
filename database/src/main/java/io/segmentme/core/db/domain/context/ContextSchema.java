package io.segmentme.core.db.domain.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context-schema")
public class ContextSchema extends DbObject {
    private SchemaNode rootNode;

    private Map<String, InlineType> inlinePath;

    @Indexed
    private String integrationPointKey;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

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
