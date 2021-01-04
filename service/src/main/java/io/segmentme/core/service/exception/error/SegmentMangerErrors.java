package io.segmentme.core.service.exception.error;

import java.util.HashMap;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;
import static io.segmentme.core.service.context.SeverityLevel.MID;

public class SegmentMangerErrors implements Errors {

    public static final String DUPLICATED_SEGMENT_KEY = "segment.manager.duplicated.segment.key";
    public static final String SEGMENT_NOT_FOUND = "segment.not.found";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(DUPLICATED_SEGMENT_KEY, CRITICAL);
            put(SEGMENT_NOT_FOUND, MID);
        }});
    }
}
