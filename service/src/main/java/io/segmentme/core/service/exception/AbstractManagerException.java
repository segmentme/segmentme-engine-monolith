package io.segmentme.core.service.exception;

import io.segmentme.core.service.context.SeverityLevel;
import io.segmentme.core.service.exception.error.Errors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractManagerException extends RuntimeException {

    private SeverityLevel severity;

    private String code;

    public AbstractManagerException() {

    }

    public AbstractManagerException(String code) {
        setCode(code);
    }

    public AbstractManagerException(Throwable cause, String code) {
        super(cause);
        setCode(code);
    }

    public AbstractManagerException(Throwable cause) {
        super(cause);
    }

    public AbstractManagerException setCode(String code) {
        this.code = code;
        this.severity = Errors.getSeverity(code);
        return this;
    }
}
