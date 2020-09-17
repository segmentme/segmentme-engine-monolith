package io.segmentme.core.service.exception;

import io.segmentme.core.service.context.ContextSchemaValidationService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class AnalysisContextValidationException extends Exception {
    private List<ContextSchemaValidationService.SchemaValidationEntry> schemaValidationResult;

}
