package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.condition.AbstractCondition

class ConditionHelper {

    static def fillCondition(Object condition, Object values, AbstractCondition.ConditionType type, boolean isEmbedded = false) {

        condition.value = values
        condition.criteria = "root.field.exist"

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
