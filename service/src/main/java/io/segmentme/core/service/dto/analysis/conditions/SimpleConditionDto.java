package io.segmentme.core.service.dto.analysis.conditions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleConditionDto<T> extends AbstractConditionDto {

    private T value;

    private boolean isNullValid;

}
