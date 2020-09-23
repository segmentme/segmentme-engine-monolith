package io.segmentme.core.api.error.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorDto extends SimpleErrorDto {

    private final List<FieldErrorDto> fieldErrors = new ArrayList<>();

    public ValidationErrorDto(ErrorType errorCategory) {
        super(errorCategory);
    }

    public ValidationErrorDto(ErrorType errorCategory, String errorMessage) {
        super(errorCategory, errorMessage);
    }

    public ValidationErrorDto(String errorMessage) {
        super(errorMessage);
    }

    public void add(String objectName, String field, String message) {
        fieldErrors.add(new FieldErrorDto(objectName, field, message));
    }

    @Data
    @AllArgsConstructor
    public static class FieldErrorDto implements Serializable {

        private String objectName;

        private String field;

        private String message;
    }
}
