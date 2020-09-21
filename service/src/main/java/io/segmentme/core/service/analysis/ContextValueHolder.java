package io.segmentme.core.service.analysis;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;

import java.util.Map;

@Data
public class ContextValueHolder {
    private Map<String, Object> values;

    private ContextSchema schema;
}
