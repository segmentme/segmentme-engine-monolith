package io.segmentme.core.service.dto.component;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleConditionDto<T> extends AbstractConditionDto<T> {

    @NotNull
    private T value;

    private boolean isNullValid;

}
