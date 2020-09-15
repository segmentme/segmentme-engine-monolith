package io.segmentme.core.db.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.exception.AnalysisContextValidationException;

import java.util.Optional;


public interface ContextSchemaService {

    ContextSchema save(ContextSchema contextSchema) throws AnalysisContextValidationException;

    Optional<ContextSchema> findById(String id);
}
