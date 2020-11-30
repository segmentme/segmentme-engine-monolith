package io.segmentme.core.service.exception.error;

import java.util.HashMap;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;

public class SegmentMangerErrors implements Errors {

    public static String DUPLICATED_SEGMENT_KEY = "segment.manager.duplicated.segment.key";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(DUPLICATED_SEGMENT_KEY, CRITICAL);
        }});
    }
}
