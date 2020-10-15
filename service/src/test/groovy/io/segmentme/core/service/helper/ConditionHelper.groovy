package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.db.domain.condition.SegmentCondition
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.service.dto.component.GroupConditionDto

class ConditionHelper {

    static def fillCondition(Object condition, Object values, AbstractCondition.ConditionType type, boolean isEmbedded = false) {
        if (condition instanceof GroupConditionDto || condition instanceof SegmentCondition) {
            condition.conditions = values
            condition.aggregation = AbstractAnalysisRule.AggregationType.AND
        } else {
            condition.value = values
            condition.criteria = "root.field.exist"
        }
        condition.type = type
        condition.name = UUID.randomUUID().toString()
        condition.description = UUID.randomUUID().toString()
        condition.matchResult = true
        condition.embedded = isEmbedded

        if (condition instanceof AbstractCondition) {
            condition.contextId == UUID.randomUUID().toString()
        }
        return condition
    }

    static def fillCondition(Object condition, Map args = [:]) {
        if (condition instanceof GroupConditionDto || condition instanceof SegmentCondition) {
            condition.conditions = args["conditions"]
            condition.aggregation = args["aggregation"] ?: AbstractAnalysisRule.AggregationType.AND
        } else {
            condition.value = args["value"]
        }
        condition.type = args["type"]
        condition.name = args["name"]
        condition.description = args["description"] ?: UUID.randomUUID().toString()
        condition.criteria = args["criteria"]
        condition.matchResult = args["matchResult"] ?: true
        condition.embedded = args["embedded"] ?: false

        if (condition instanceof AbstractCondition) {
            condition.contextId == args["contextId"] ?: UUID.randomUUID().toString()
        }
        return condition
    }
}
