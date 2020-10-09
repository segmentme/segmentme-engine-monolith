package io.segmentme.core.api.dto;

import io.segmentme.core.service.context.ContextSchemaValidationService;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ContextSchemaValidationResult {
    private ContextSchemaDetails contextSchema;

    private List<ContextSchemaValidationService.SchemaValidationEntry> validationEntries;

   private Map<String, Object> nodeValues;
}
