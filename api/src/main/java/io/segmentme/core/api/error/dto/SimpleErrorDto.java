package io.segmentme.core.api.error.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SimpleErrorDto implements ErrorMessage {

    private ErrorType errorType = ErrorType.UNKNOWN_ERROR;

    private String message;

    public SimpleErrorDto(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }

    public SimpleErrorDto(String errorMessage) {
        this.message = errorMessage;
    }

    public SimpleErrorDto(ErrorType errorType) {
        this.errorType = errorType;
    }
}
