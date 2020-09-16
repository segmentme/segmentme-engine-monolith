package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;

import java.util.Map;

@Data
public class ContextHolder {
    private Map<String, Object> values;

    private ContextSchema schema;
}
