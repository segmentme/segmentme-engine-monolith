package io.segmentme.core.service.exception.error;

import java.util.HashMap;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;
import static io.segmentme.core.service.context.SeverityLevel.MID;

public class CriteriaValueLocatorErrors implements Errors {

    public static String CRITERIA_NOT_FOUND = "criteria.value.locator.criteria.not.found";
    public static String UNEXPECTED_LOCATOR_ERROR = "criteria.value.locator.unexpected.error";
    public static String UNEXPECTED_ARRAY_TYPE = "criteria.value.locator.expected.single.but.found.array";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(CRITERIA_NOT_FOUND, MID);
            put(UNEXPECTED_LOCATOR_ERROR, MID);
            put(UNEXPECTED_ARRAY_TYPE, CRITICAL);
        }});
    }
}
