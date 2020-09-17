package io.segmentme.core.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import static io.segmentme.core.db.service.context.ContextSchemaValidationService.SchemaValidationEntry;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnalysisContextValidationException extends Exception {
    private List<SchemaValidationEntry> schemaValidationResult;

}
