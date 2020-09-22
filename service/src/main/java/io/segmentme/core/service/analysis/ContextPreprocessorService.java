package io.segmentme.core.service.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;

public interface ContextPreprocessorService {

    ContextValueHolder prepareContext(JsonNode rawContext, ContextSchema schema, WorkspaceConfiguration workspaceConfiguration);
}
