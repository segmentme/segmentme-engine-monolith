package io.segmentme.core.service.exception.error;

import io.segmentme.core.service.context.SeverityLevel;

import java.util.Map;

public class StateManagerErrors implements Errors {

    public static final String STATE_NOT_FOUND = "sate.manager.not.found";
    public static final String DUPLICATED_STATE_NAME = "sate.manager.duplicated.name";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
                STATE_NOT_FOUND, SeverityLevel.CRITICAL,
                DUPLICATED_STATE_NAME, SeverityLevel.CRITICAL
        ));
    }
}
