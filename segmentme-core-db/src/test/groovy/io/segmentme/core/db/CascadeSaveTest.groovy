package io.segmentme.core.db

import io.segmentme.core.db.common.BaseDatabaseTest
import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.db.repository.SimpleAnalysisRuleRepository
import io.segmentme.core.db.repository.PreconditionAnalysisRuleRepository
import org.springframework.beans.factory.annotation.Autowired

class CascadeSaveTest extends BaseDatabaseTest {

    @Autowired
    private SimpleAnalysisRuleRepository analysisRuleRepository
    @Autowired
    private PreconditionAnalysisRuleRepository preconditionAnalysisRuleRepository
    @Autowired
    private AbstractConditionRepository abstractConditionRepository

    def 'cascade save: Success'() {
        setup:
        def date = prepareDate()
        when:
        preconditionAnalysisRuleRepository.save(date)
        def find = preconditionAnalysisRuleRepository.findAll().first()
        find.value = false
        preconditionAnalysisRuleRepository.save(find)
        then:
        def analysisRules = analysisRuleRepository.findAll()
        def preconditionAnalysisRules = preconditionAnalysisRuleRepository.findAll()
        def conditions = abstractConditionRepository.findAll()

        preconditionAnalysisRules != null
        analysisRules != null
        conditions != null

        preconditionAnalysisRules.size() == 1
        def preconditionAnalysisRule = preconditionAnalysisRules.first()
        preconditionAnalysisRule.ruleType == AbstractAnalysisRule.RuleType.PRECONDITION
        preconditionAnalysisRule.analysisRules != null
        def embeddedAnalysisRule = preconditionAnalysisRule.analysisRules.first()
        analysisRules.size() == 1
        def analysisRule = analysisRules.first()
        embeddedAnalysisRule.getId() == analysisRule.getId()
        conditions.size() == 1
        embeddedAnalysisRule.conditions.first().id == analysisRule.conditions.first().id
    }

    private PreconditionAnalysisRule prepareDate() {
        def analysisRule = new BooleanAnalysisRule()
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(new ArrayCondition()
                        .setCriteria("memberType")
                        .setType(AbstractCondition.ConditionType.CONTAINS_ALL)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME"))
                )
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)

        return new PreconditionAnalysisRule()
                .setAnalysisRules(Arrays.asList(analysisRule))
                .setRuleType(AbstractAnalysisRule.RuleType.PRECONDITION)
                .setValue(true)
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
    }
}
