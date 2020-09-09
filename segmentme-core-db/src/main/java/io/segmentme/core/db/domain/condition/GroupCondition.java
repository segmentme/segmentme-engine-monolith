package io.segmentme.core.db.domain.condition;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GroupCondition extends AbstractCondition<List<AbstractCondition<?>>> {

    private AbstractAnalysisRule.AggregationType aggregation;

    private List<AbstractCondition<?>> conditions;
}
