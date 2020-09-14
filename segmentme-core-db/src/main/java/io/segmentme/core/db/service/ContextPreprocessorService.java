package io.segmentme.core.db.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.AnalysisContextSchema;

public interface ContextPreprocessorService {

    AnalysisContext prepareContext(JsonNode rawContext, AnalysisContextSchema schema);
}
