package io.segmentme.core.service.converter

import io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType
import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.condition.SingleCondition
import io.segmentme.core.service.dto.analysis.conditions.ArrayConditionDto
import io.segmentme.core.service.dto.analysis.conditions.SingleConditionDto
import spock.lang.Specification

import static io.segmentme.core.service.helper.ConditionHelper.fillCondition

class ConditionConverterTest extends Specification {

    def "array condition dto converting: #type and val: #values isDto: #isDto"() {
        given:
        def source = fillCondition(isDto ? new ArrayConditionDto() : new ArrayCondition(), values, type)
        expect:
        def target = isDto ? ConditionConverter.of(source) : ConditionConverter.of(source)
        target.type == source.type
        target.value == source.value
        target.criteria == source.criteria
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
        def target = isDto ? ConditionConverter.of(source) : ConditionConverter.of(source)
        target.type == source.type
        target.value == source.value
        target.criteria == source.criteria
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
}
