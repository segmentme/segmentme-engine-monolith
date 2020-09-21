package io.segmentme.core.service.dto.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;

import java.util.Map;

@Data
public class ContextSchemaHolder {
    public String id;

    private SchemaNode rootNode;

    private Map<String, ContextSchema.InlineType> inlinePath;

    private String integrationPointKey;

}
