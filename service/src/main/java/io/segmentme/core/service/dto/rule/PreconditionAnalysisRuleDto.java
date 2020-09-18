package io.segmentme.core.service.dto.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PreconditionAnalysisRuleDto extends AbstractAnalysisRuleDto<Boolean> {

    @NotEmpty
    private List<? extends AbstractAnalysisRuleDto<?>> analysisRules;
}
