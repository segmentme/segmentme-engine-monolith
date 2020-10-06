package io.segmentme.core.api.dto;

import io.segmentme.core.service.context.ContextSchemaValidationService;
import lombok.Data;

import java.util.List;

@Data
public class ContextSchemaValidationResult {
    private ContextSchemaDetails contextSchema;

    private List<ContextSchemaValidationService.SchemaValidationEntry> validationEntries;
}
