package io.segmentme.core.service.exception.error;

import java.util.HashMap;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;

public class ContextMangerErrors implements Errors {

    public static String CONTEXT_NOT_FOUND = "context.manager.context.not.found";
    public static String INTEGRATION_POINT_NOT_FOUND = "context.manager.integration.point.not.found";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(CONTEXT_NOT_FOUND, CRITICAL);
            put(INTEGRATION_POINT_NOT_FOUND, CRITICAL);
        }});
    }
}
