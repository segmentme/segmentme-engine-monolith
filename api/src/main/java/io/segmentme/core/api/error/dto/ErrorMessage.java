package io.segmentme.core.api.error.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ErrorMessage extends Serializable {

    ErrorType getErrorType();

    String getMessage();

}
