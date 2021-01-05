package io.segmentme.core.service.exception.error;

import java.util.Map;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;

public class UserManagerErrors implements Errors {

    public static final String USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE = "user.manager.user.should.not.have.id";
    public static final String UNABLE_TO_SWITCH_WORKSPACE_DOESNT_EXISTS = "user.manager.workspace.doesnt.exists";
    public static final String USER_WITH_SUCH_EMAIL_ALREADY_EXISTS = "user.manager.user.with.email.already.exists";
    public static final String USER_NOT_FOUND = "user.manager.user.not.found";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
            USER_SHOULD_NOT_HAVE_ID_ATTRIBUTE, CRITICAL,
            USER_WITH_SUCH_EMAIL_ALREADY_EXISTS, CRITICAL,
            UNABLE_TO_SWITCH_WORKSPACE_DOESNT_EXISTS, CRITICAL,
            USER_NOT_FOUND, CRITICAL
            ));
    }
}
