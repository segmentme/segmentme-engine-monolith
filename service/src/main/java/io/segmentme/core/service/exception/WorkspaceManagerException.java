package io.segmentme.core.service.exception;

import io.segmentme.core.service.context.SeverityLevel;
import io.segmentme.core.service.exception.error.Errors;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class WorkspaceManagerException extends RuntimeException {
    private String code;
    private SeverityLevel severity;

    public WorkspaceManagerException setCode(String code) {
        this.code = code;
        this.severity = Errors.getSeverity(code);
        return this;
    }
}
