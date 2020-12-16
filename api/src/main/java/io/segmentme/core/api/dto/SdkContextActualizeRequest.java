package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.context.SchemaNode;
import lombok.Data;

@Data
public class SdkContextActualizeRequest {
    private SchemaNode schemaNode;

    private String rawPayload;

    private String contextKey;
}
