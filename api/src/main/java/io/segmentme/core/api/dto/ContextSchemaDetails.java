package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;

import java.util.Map;

@Data
public class ContextSchemaDetails {
    private SchemaNode rootNode;

    private Map<String, ContextSchema.InlineType> inlinePath;

    private String integrationPointKey;

    private String integrationPointName;
}
