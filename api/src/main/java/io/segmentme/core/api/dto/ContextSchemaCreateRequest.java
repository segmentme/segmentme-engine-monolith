package io.segmentme.core.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;

@Data
public class ContextSchemaCreateRequest {
    private SchemaNode rootNode;

    private String integrationPointKey;

    private JsonNode rawPayload;

}
