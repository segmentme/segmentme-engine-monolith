package io.segmentme.core.service.exception.error;

import io.segmentme.core.service.context.SeverityLevel;

import java.util.Map;

public class StateManagerErrors implements Errors {

    public static String STATE_NOT_FOUND = "sate.not.found";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
                STATE_NOT_FOUND, SeverityLevel.CRITICAL
        ));
    }
}
