package io.segmentme.core.service.exception.error;

import java.util.Map;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;
import static io.segmentme.core.service.context.SeverityLevel.MID;

public class CriteriaValueLocatorErrors implements Errors {

    public static final String CRITERIA_NOT_FOUND = "criteria.value.locator.criteria.not.found";
    public static final String UNEXPECTED_LOCATOR_ERROR = "criteria.value.locator.unexpected.error";
    public static final String UNEXPECTED_ARRAY_TYPE = "criteria.value.locator.expected.single.but.found.array";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
            CRITERIA_NOT_FOUND, MID,
            UNEXPECTED_LOCATOR_ERROR, MID,
            UNEXPECTED_ARRAY_TYPE, CRITICAL
        ));
    }
}
