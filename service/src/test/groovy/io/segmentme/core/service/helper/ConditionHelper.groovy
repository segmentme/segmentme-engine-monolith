package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.db.domain.condition.GroupCondition
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.service.dto.component.GroupConditionDto

public class ConditionHelper {

    public static def fillCondition(Object condition, Object values, AbstractCondition.ConditionType type, boolean isEmbedded = false) {
        if (condition instanceof GroupConditionDto || condition instanceof GroupCondition) {
            condition.conditions = values
            condition.aggregation = AbstractAnalysisRule.AggregationType.AND
        } else {
            condition.value = values
        }
        condition.type = type
        condition.name = UUID.randomUUID().toString()
        condition.description = UUID.randomUUID().toString()
        condition.criteria = "root.field.exist"
        condition.matchResult = true
        condition.embedded = isEmbedded

        if (condition instanceof AbstractCondition) {
            condition.contextId == UUID.randomUUID().toString()
        }
        return condition
    }
}
