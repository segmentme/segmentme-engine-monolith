package io.segmentme.core.service.exception.error;

import java.util.Map;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;

public class ContextMangerErrors implements Errors {

    public static final String CONTEXT_NOT_FOUND = "context.manager.context.not.found";
    public static final String INTEGRATION_POINT_NOT_FOUND = "context.manager.integration.point.not.found";
    public static final String INVALID_JSON = "context.manager.invalid.json";
    public static final String WORKSPACE_NOT_FOUND = "context.manager.workspace.not.found";

    static {

        ERRORS_SEVERITY.putAll(Map.of(
            CONTEXT_NOT_FOUND, CRITICAL,
            INTEGRATION_POINT_NOT_FOUND, CRITICAL,
            INVALID_JSON, CRITICAL,
            WORKSPACE_NOT_FOUND, CRITICAL
        ));
    }
}
