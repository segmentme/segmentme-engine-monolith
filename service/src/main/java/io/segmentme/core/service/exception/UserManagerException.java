package io.segmentme.core.service.exception;

import io.segmentme.core.service.context.SeverityLevel;
import io.segmentme.core.service.exception.error.Errors;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class UserManagerException extends Exception {
    private String code;
    private SeverityLevel severity;

    public UserManagerException setCode(String code) {
        this.code = code;
        this.severity = Errors.getSeverity(code);
        return this;
    }
}
