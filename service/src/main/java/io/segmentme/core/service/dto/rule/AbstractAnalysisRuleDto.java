package io.segmentme.core.service.dto.rule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "ruleType", visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "BOOLEAN", value = BooleanAnalysisRuleDto.class),
        @JsonSubTypes.Type(name = "PRECONDITION", value = PreconditionAnalysisRuleDto.class),
        @JsonSubTypes.Type(name = "JSON", value = JsonAnalysisRuleDto.class)
})
public abstract class AbstractAnalysisRuleDto<T> {

    private AbstractAnalysisRule.RuleType ruleType;

    @NotNull
    private AbstractAnalysisRule.AggregationType aggregation;

    private List<AbstractCondition<?>> conditions;

    private boolean embedded;

    @NotNull
    public T value;

}
