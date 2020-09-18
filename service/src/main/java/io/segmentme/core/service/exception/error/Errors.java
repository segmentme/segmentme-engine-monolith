package io.segmentme.core.service.exception.error;

import io.segmentme.core.service.context.SeverityLevel;

import java.util.HashMap;
import java.util.Map;

import static io.segmentme.core.service.context.SeverityLevel.MID;

public interface Errors {

    Map<String, SeverityLevel> ERRORS_SEVERITY = new HashMap<>();

    static SeverityLevel getSeverity(String error) {
        return ERRORS_SEVERITY.getOrDefault(error, MID);
    }
}
