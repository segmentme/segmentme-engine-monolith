package io.segmentme.core.service.converter

import io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType
import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.condition.GroupCondition
import io.segmentme.core.db.domain.condition.SingleCondition
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.dto.component.GroupConditionDto
import io.segmentme.core.service.dto.component.SingleConditionDto
import spock.lang.Specification

import static io.segmentme.core.service.helper.ConditionHelper.fillCondition

class ConditionConverterTest extends Specification {

    def "array condition dto converting: #type and val: #values isDto: #isDto"() {
        given:
        def source = fillCondition(isDto ? new ArrayConditionDto() : new ArrayCondition(), values, type)
        expect:
        def target = isDto ? ConditionConverter.of(source, UUID.randomUUID().toString()) : ConditionConverter.of(source)
        target.name == source.name
        target.type == source.type
        target.value == source.value
        target.criteria == source.criteria
        target.description == source.description
        target.matchResult == source.matchResult
        where:
        type                        | values                         | isDto
        ConditionType.IN            | [1, 2, 3]                      | true
        ConditionType.IN            | ["test_1", "test_2", "test_3"] | true
        ConditionType.CONTAINS_ONLY | ["VALUE"]                      | true
        ConditionType.CONTAINS_ONLY | [true]                         | true
        ConditionType.CONTAINS_ANY  | [123.31, 12.3, 41]             | true
        ConditionType.CONTAINS_ANY  | ["VALUE_1", "VALUE_2"]         | true
        ConditionType.CONTAINS_ALL  | [1]                            | true
        ConditionType.CONTAINS_ALL  | ["212", "31d"]                 | true
        ConditionType.IN            | [1, 2, 3]                      | false
        ConditionType.IN            | ["test_1", "test_2", "test_3"] | false
        ConditionType.CONTAINS_ONLY | ["VALUE"]                      | false
        ConditionType.CONTAINS_ONLY | [true]                         | false
        ConditionType.CONTAINS_ANY  | [123.31, 12.3, 41]             | false
        ConditionType.CONTAINS_ANY  | ["VALUE_1", "VALUE_2"]         | false
        ConditionType.CONTAINS_ALL  | [1]                            | false
        ConditionType.CONTAINS_ALL  | ["212", "31d"]                 | false
    }


    def "single condition dto converting: #type and val: #values isDto: #isDto"() {
        given:
        def source = fillCondition(isDto ? new SingleConditionDto() : new SingleCondition(), values, type)
        expect:
        def target = isDto ? ConditionConverter.of(source, UUID.randomUUID().toString()) : ConditionConverter.of(source)
        target.name == source.name
        target.type == source.type
        target.value == source.value
        target.criteria == source.criteria
        target.description == source.description
        target.matchResult == source.matchResult
        where:
        type              | values       | isDto
        ConditionType.LTE | 1            | true
        ConditionType.LT  | "1990-12-31" | true
        ConditionType.GTE | 500          | true
        ConditionType.GT  | 413          | true
        ConditionType.LTE | 1            | false
        ConditionType.LT  | "1990-12-31" | false
        ConditionType.GTE | 500          | false
        ConditionType.GT  | 413          | false
    }


    def "group condition dto converting: #type and val: #values isDto: #isDto"() {
        given:
        def source = fillCondition(isDto ? new GroupConditionDto() : new GroupCondition(), values, type)
        expect:
        def target = isDto ? ConditionConverter.of(source, UUID.randomUUID().toString()) : ConditionConverter.of(source)
        target.name == source.name
        target.type == source.type
        target.description == source.description
        target.matchResult == source.matchResult
        target.aggregation == source.aggregation
        where:
        type                | values                                                                                                                          | isDto
        ConditionType.GROUP | [fillCondition(new ArrayConditionDto(), [1], ConditionType.IN)]                                                                 | true
        ConditionType.GROUP | [fillCondition(new ArrayConditionDto(), [1], ConditionType.IN), fillCondition(new SingleConditionDto(), 500, ConditionType.LT)] | true
        ConditionType.GROUP | [fillCondition(new ArrayConditionDto(), [1], ConditionType.IN), fillCondition(new SingleConditionDto(), 500, ConditionType.LT)] | true
        ConditionType.GROUP | [fillCondition(new GroupConditionDto(), [fillCondition(new ArrayConditionDto(), [1], ConditionType.IN)], ConditionType.GROUP)]  | true
        ConditionType.GROUP | [fillCondition(new ArrayCondition(), [1], ConditionType.IN)]                                                                    | false
        ConditionType.GROUP | [fillCondition(new ArrayCondition(), [1], ConditionType.IN), fillCondition(new SingleCondition(), 500, ConditionType.LT)]       | false
        ConditionType.GROUP | [fillCondition(new ArrayCondition(), [1], ConditionType.IN), fillCondition(new SingleCondition(), 500, ConditionType.LT)]       | false
        ConditionType.GROUP | [fillCondition(new GroupCondition(), [fillCondition(new ArrayCondition(), [1], ConditionType.IN)], ConditionType.GROUP)]        | false
    }
}
