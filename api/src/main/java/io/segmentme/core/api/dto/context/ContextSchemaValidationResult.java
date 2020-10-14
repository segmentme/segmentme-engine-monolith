package io.segmentme.core.api.dto.context;

import io.segmentme.core.service.context.ContextSchemaValidationService;
import lombok.Data;

import java.util.List;

@Data
public class ContextSchemaValidationResult {
    private List<ContextSchemaValidationService.SchemaValidationEntry> validationEntries;
}
