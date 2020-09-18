package io.segmentme.core.service.dto.component;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupConditionDto extends AbstractConditionDto<List<AbstractConditionDto<?>>> {

    @NotNull
    private AbstractAnalysisRule.AggregationType aggregation;

    @NotBlank
    private List<AbstractConditionDto<?>> conditions;
}
