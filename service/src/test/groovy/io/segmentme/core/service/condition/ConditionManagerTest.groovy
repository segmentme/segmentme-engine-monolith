package io.segmentme.core.service.condition

import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.dto.analysis.conditions.ArrayConditionDto
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.service.helper.ConditionHelper.fillCondition

class ConditionManagerTest extends BaseTestWithContext {

    @Autowired
    private ConditionManager conditionManager

    def 'create condition: type = #type values = #values'() {
        given:
        def source = fillCondition(new ArrayConditionDto(), values, type)
        expect:
        def target = conditionManager.create(source, UUID.randomUUID().toString())
        target.id != null
        target.name == source.name
        target.type == source.type
        target.value == source.value
        target.criteria == source.criteria
        target.description == source.description
        target.matchResult == source.matchResult
        where:
        type                                          | values
        AbstractCondition.ConditionType.IN            | [1, 2, 3]
        AbstractCondition.ConditionType.IN            | ["test_1", "test_2", "test_3"]
        AbstractCondition.ConditionType.CONTAINS_ONLY | ["VALUE"]
        AbstractCondition.ConditionType.CONTAINS_ONLY | [true]
        AbstractCondition.ConditionType.CONTAINS_ANY  | [123.31, 12.3, 41]
        AbstractCondition.ConditionType.CONTAINS_ANY  | ["VALUE_1", "VALUE_2"]
        AbstractCondition.ConditionType.CONTAINS_ALL  | [1]
        AbstractCondition.ConditionType.CONTAINS_ALL  | ["212", "31d"]
    }


//    def "create group condition: type = #type values = #values"() {
//        given:
//        def source = fillCondition(new GroupConditionDto(), values, type)
//        expect:
//        def target = conditionManager.create(source, UUID.randomUUID().toString())
//        target.id != null
//        target.name == source.name
//        target.type == source.type
//        target.description == source.description
//        target.matchResult == source.matchResult
//        target.aggregation == source.aggregation
//        target.conditions.size() > 0
//
//        def embeddedConditionSource = source.conditions[0]
//        def embeddedConditionTarget = target.conditions[0]
//        embeddedConditionTarget.id != null
//        embeddedConditionTarget.name == embeddedConditionSource.name
//        embeddedConditionTarget.type == embeddedConditionSource.type
//        embeddedConditionTarget.description == embeddedConditionSource.description
//        embeddedConditionTarget.matchResult == embeddedConditionSource.matchResult
//
//        if (embeddedConditionTarget instanceof GroupConditionDto) {
//            embeddedConditionTarget.conditions.size() > 0
//            embeddedConditionTarget.embedded
//        }
//
//        where:
//        type                                  | values
//        AbstractCondition.ConditionType.SEGMENT | [fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN)]
//        AbstractCondition.ConditionType.SEGMENT | [fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN), fillCondition(new SingleConditionDto(), 500, AbstractCondition.ConditionType.LT)]
//        AbstractCondition.ConditionType.SEGMENT | [fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN), fillCondition(new SingleConditionDto(), 500, AbstractCondition.ConditionType.LT)]
//        AbstractCondition.ConditionType.SEGMENT | [fillCondition(new GroupConditionDto(), [fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN, true)], AbstractCondition.ConditionType.SEGMENT, true)]
//    }


    def "create simple condition find by contextId"() {
        given:
        def contextId = UUID.randomUUID().toString()
        def condition = fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN)
        conditionManager.create(condition, contextId)
        when:
        def existedContexts = conditionManager.findByContextId(contextId)
        then:
        existedContexts.size() == 1
        def existedContext = existedContexts[0]
        existedContext.id != null
    }


//    def "create group condition and find by contextId"() {
//        given:
//        def contextId = UUID.randomUUID().toString()
//        def condition = fillCondition(new GroupConditionDto(), [fillCondition(new ArrayConditionDto(), [1], AbstractCondition.ConditionType.IN, true)], AbstractCondition.ConditionType.SEGMENT)
//        conditionManager.create(condition, contextId)
//        when:
//        def existedContexts = conditionManager.findByContextId(contextId)
//        then:
//        existedContexts.size() == 1
//    }
}
