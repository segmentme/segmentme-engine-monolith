package io.segmentme.core.service.exception.error;

import java.util.HashMap;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;

public class UserManagerErrors implements Errors {

    public static final String USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE = "user.manager.user.should.not.have.id";
    public static final String UNABLE_TO_SWITCH_WORKSPACE_DOESNT_EXISTS = "user.manager.workspace.doesnt.exists";
    public static final String USER_WITH_SUCH_EMAIL_ALREADY_EXISTS = "user.manager.user.with.email.already.exists";

    static {
        ERRORS_SEVERITY.putAll(new HashMap<>() {{
            put(USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE, CRITICAL);
            put(USER_WITH_SUCH_EMAIL_ALREADY_EXISTS, CRITICAL);
            put(UNABLE_TO_SWITCH_WORKSPACE_DOESNT_EXISTS, CRITICAL);
        }});
    }
}
