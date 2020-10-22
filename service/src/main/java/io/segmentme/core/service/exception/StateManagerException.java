package io.segmentme.core.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StateManagerException extends AbstractManagerException {

    public StateManagerException(String message, String code) {
        super(message, code);
    }
}
