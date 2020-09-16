package io.segmentme.core.db.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.service.ContextHolder;

public interface ContextPreprocessorService {

    ContextHolder prepareContext(JsonNode rawContext, ContextSchema schema);
}
