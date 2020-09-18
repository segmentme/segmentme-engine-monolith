package io.segmentme.core.service.dto.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanAnalysisRuleDto extends SimpleAnalysisRuleDto<Boolean> {
}
