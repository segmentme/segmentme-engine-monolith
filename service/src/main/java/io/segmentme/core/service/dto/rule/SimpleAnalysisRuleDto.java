package io.segmentme.core.service.dto.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleAnalysisRuleDto<T> extends AbstractAnalysisRuleDto<T> {

    @NotEmpty
    private List<String> flags;

}
