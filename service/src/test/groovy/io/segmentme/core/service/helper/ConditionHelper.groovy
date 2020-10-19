package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.condition.AbstractCondition

class ConditionHelper {

    static def fillCondition(Object condition, Object values, AbstractCondition.ConditionType type, boolean isEmbedded = false) {

        condition.value = values
        condition.criteria = "root.field.exist"

        condition.type = type
        condition.description = UUID.randomUUID().toString()
        condition.matchResult = true
        return condition
    }

    static def fillCondition(Object condition, Map args = [:]) {
        condition.type = args["type"]
        condition.description = args["description"] ?: UUID.randomUUID().toString()
        condition.criteria = args["criteria"]
        condition.matchResult = args["matchResult"] ?: true
        return condition
    }
}
