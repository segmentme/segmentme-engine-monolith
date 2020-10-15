package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule

import io.segmentme.core.service.dto.rule.PreconditionAnalysisRuleDto
import io.segmentme.core.service.dto.rule.SimpleAnalysisRuleDto

class RuleHelper {

    static def fillRule(Object rule, Map args = [:]) {
        rule.value = args['value']
        rule.ruleType = args['ruleType']
        rule.conditions = args['conditions']
        rule.embedded = args['embedded'] ?: false
        rule.aggregation = args['aggregation'] ?: AbstractAnalysisRule.AggregationType.AND

        if (rule instanceof SimpleAnalysisRuleDto || rule instanceof SimpleAnalysisRule) {
            rule.flags = args['flags'] ?: List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
        }

        if (rule instanceof PreconditionAnalysisRuleDto || rule instanceof PreconditionAnalysisRule) {
            rule.analysisRules = args['analysisRules']
        }

        return rule
    }
}
