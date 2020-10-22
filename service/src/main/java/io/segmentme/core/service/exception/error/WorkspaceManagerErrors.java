package io.segmentme.core.service.exception.error;

import io.segmentme.core.service.context.SeverityLevel;

import java.util.HashMap;

public class WorkspaceManagerErrors implements Errors {
    public static String UNABLE_TO_DELETE_DEFAULT_WORKSAPCE = "workspace.manager.unable.to.delete.default.workspace";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(UNABLE_TO_DELETE_DEFAULT_WORKSAPCE, SeverityLevel.CRITICAL);
        }});
    }
}
