package io.segmentme.core.service.converter

import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.condition.SingleCondition
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule
import io.segmentme.core.db.domain.rule.JsonAnalysisRule
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.dto.component.SingleConditionDto
import io.segmentme.core.service.dto.rule.BooleanAnalysisRuleDto
import io.segmentme.core.service.dto.rule.JsonAnalysisRuleDto
import io.segmentme.core.service.dto.rule.PreconditionAnalysisRuleDto
import io.segmentme.core.service.dto.rule.SimpleAnalysisRuleDto
import spock.lang.Specification

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule
import static java.util.UUID.randomUUID

class RuleConverterTest extends Specification {

    def "Analysis rule converting: #values isDto: #isDto"() {
        given:
        def source = createRule(isDto, values)
        expect:
        def target = isDto ? RuleConverter.of(source, randomUUID().toString(), randomUUID().toString()) : RuleConverter.of(source)
        target.value == source.value
        if (target instanceof SimpleAnalysisRule || target instanceof SimpleAnalysisRuleDto) {
            target.flags.size() == 3
        }
        target.ruleType == source.ruleType
        target.embedded == source.embedded
        target.aggregation == source.aggregation
        target.conditions.size() == 1
        def convertedCondition = target.conditions[0]
        def condition = source.conditions[0]
        convertedCondition.type == condition.type
        where:
        values                                                                                                                                                      | isDto
        ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]                                               | true
        ['value': false, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]                                              | true
        ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]                                                  | false
        ['value': false, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]                                                 | false
        ['value': "JSON_VALUE", 'ruleType': JSON, 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT))]                                            | true
        ['value': "JSON_VALUE", 'ruleType': JSON, 'conditions': List.of(fillCondition(new SingleConditionDto(), 2, LTE))]                                           | true
        ['value': "JSON_VALUE", 'ruleType': JSON, 'conditions': List.of(fillCondition(new SingleCondition(), 1, LT))]                                               | false
        ['value': "JSON_VALUE", 'ruleType': JSON, 'conditions': List.of(fillCondition(new SingleCondition(), 2, LTE))]                                              | false
        ['value'        : true, 'ruleType': PRECONDITION, 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT)),
         'analysisRules': List.of(createRule(true, ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), true, IN))]))] | true
        ['value'        : true, 'ruleType': PRECONDITION, 'conditions': List.of(fillCondition(new SingleCondition(), 1, LT)),
         'analysisRules': List.of(createRule(false, ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayCondition(), true, IN))]))]   | false
    }

    private def createRule(boolean isDto, Map values) {
        switch (values['ruleType']) {
            case BOOLEAN: return fillRule(isDto ? new BooleanAnalysisRuleDto() : new BooleanAnalysisRule(), values)
            case JSON: return fillRule(isDto ? new JsonAnalysisRuleDto() : new JsonAnalysisRule(), values)
            case PRECONDITION: return fillRule(isDto ? new PreconditionAnalysisRuleDto() : new PreconditionAnalysisRule(), values)
        }
    }
}
