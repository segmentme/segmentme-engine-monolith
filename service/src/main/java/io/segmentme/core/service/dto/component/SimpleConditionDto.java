package io.segmentme.core.service.dto.component;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleConditionDto<T> extends AbstractConditionDto<T> {

    private T value;

    private boolean isNullValid;

    @NotBlank
    private String criteria;
}
