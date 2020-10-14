package io.segmentme.core.api.dto.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;

import java.util.Map;

@Data
public class ContextSchemaBasicInfo extends ContextSchemaShortInfo {
    private Map<String, ContextSchema.InlineType> inlinePath;
}
