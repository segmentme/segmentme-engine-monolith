package io.segmentme.core.service.rule

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.dto.component.SingleConditionDto
import io.segmentme.core.service.dto.rule.BooleanAnalysisRuleDto
import io.segmentme.core.service.dto.rule.JsonAnalysisRuleDto
import io.segmentme.core.service.dto.rule.PreconditionAnalysisRuleDto
import io.segmentme.core.service.dto.rule.SimpleAnalysisRuleDto
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule

class RuleManagerTest extends BaseTestWithContext {

    private static final ObjectMapper MAPPER = new ObjectMapper()

    @Autowired
    private RuleManager ruleManager

    def "Analysis rule converting:"() {
        given:
        def integrationPointKey = UUID.randomUUID().toString()
        def contextId = UUID.randomUUID().toString()
        def source = createRule(values)
        ruleManager.save(List.of(source), contextId, integrationPointKey)

        expect:
        def existedRules = ruleManager.findByIntegrationPointKey(integrationPointKey)
        existedRules.size() == 1
        def rule = existedRules[0]
        rule.value == source.value
        if (rule instanceof SimpleAnalysisRule || rule instanceof SimpleAnalysisRuleDto) {
            rule.flags.size() == 3
        }
        rule.ruleType == source.ruleType
        rule.embedded == source.embedded
        rule.aggregation == source.aggregation
        rule.conditions.size() == 1
        def convertedCondition = rule.conditions[0]
        def condition = source.conditions[0]
        convertedCondition.type == condition.type
        where:
        values                                                                                                                                                                  | _
        ['value': MAPPER.convertValue(new int[]{1,2,3,4,5}, JsonNode.class), 'ruleType': JSON, 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT))]                    | _
    }

    public static def createRule(Map values) {
        switch (values['ruleType']) {
            case BOOLEAN: return fillRule(new BooleanAnalysisRuleDto(), values)
            case JSON: return fillRule(new JsonAnalysisRuleDto(), values)
            case PRECONDITION: return fillRule(new PreconditionAnalysisRuleDto(), values)
        }
    }
}
