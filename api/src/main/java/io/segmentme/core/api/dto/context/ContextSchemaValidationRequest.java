package io.segmentme.core.api.dto.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;

@Data
public class ContextSchemaValidationRequest {
    private SchemaNode rootNode;

    private JsonNode rawPayload;
}
