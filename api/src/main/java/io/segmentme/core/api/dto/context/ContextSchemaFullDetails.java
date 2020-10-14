package io.segmentme.core.api.dto.context;

import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContextSchemaFullDetails extends ContextSchemaBasicInfo {
    private String rawPayload;

    private SchemaNode rootNode;

    private Map<String, Object> nodeValues;
}
