package io.segmentme.core.service.dto.analysis.component;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleConditionDto<T> extends AbstractConditionDto {

    private T value;

    private boolean isNullValid;

    @NotBlank
    private String criteria;
}
