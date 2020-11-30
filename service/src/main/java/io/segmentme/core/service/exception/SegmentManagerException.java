package io.segmentme.core.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class SegmentManagerException extends AbstractManagerException {

    public SegmentManagerException(String code) {
        super(code);
    }
}
