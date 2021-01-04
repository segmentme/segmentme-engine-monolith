package io.segmentme.core.api.dto.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContextSchemaBasicInfo extends ContextSchemaShortInfo {
    private Map<String, ContextSchema.InlineType> inlinePath;
}
