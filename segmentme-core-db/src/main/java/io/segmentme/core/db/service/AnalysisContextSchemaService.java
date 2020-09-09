package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.exception.AnalysisContextValidationException;

import java.util.Optional;


public interface AnalysisContextSchemaService {

    AnalysisContextSchema save(AnalysisContextSchema analysisContextSchema) throws AnalysisContextValidationException;

    Optional<AnalysisContextSchema> findById(String id);
}
