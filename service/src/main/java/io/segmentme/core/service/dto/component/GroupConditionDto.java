package io.segmentme.core.service.dto.component;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupConditionDto extends AbstractConditionDto<List<AbstractConditionDto<?>>> {

    @NotNull
    private AbstractAnalysisRule.AggregationType aggregation;

    @Valid
    @NotBlank
    private List<AbstractConditionDto<?>> conditions;
}
