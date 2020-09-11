package io.segmentme.core.db.service.rule

import io.segmentme.core.db.common.BaseDatabaseTest
import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.context.AnalysisContextSchema
import io.segmentme.core.db.domain.context.SchemaNode
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule
import io.segmentme.core.db.dto.AnalysisResult
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository
import io.segmentme.core.db.repository.AbstractConditionRepository
import org.springframework.beans.factory.annotation.Autowired

class AnalysisServiceTest extends BaseDatabaseTest {

    @Autowired
    private AbstractAnalysisRuleRepository analysisRuleRepository
    @Autowired
    private AbstractConditionRepository abstractConditionRepository
    @Autowired
    private AnalysisService analysisRuleService;

    def 'cascade save: Success'() {
        setup:
        prepareDate()
        when:
        def result = analysisRuleService.analyze(new AnalysisContextSchema().setRootNode(new SchemaNode().setName("VALUE")))
        then:
        result != null
        result.size() == 3
        !isMatched("TEST_FLAG_1", result)
        isMatched("TEST_FLAG_2", result)
        !isMatched("TEST_FLAG_3", result)
    }


    private Boolean isMatched(String flagName, List<AnalysisResult> results){
        return results.stream()
                .filter(it -> it.getNames().contains(flagName))
                .findFirst()
                .map(it -> it.getValue())
                .map(it -> Boolean.TRUE.equals(it))
                .orElse(null)
    }

    private void prepareDate() {

        def conditions = createConditions()
        def analysisRule = new BooleanAnalysisRule()
                .setFlags(Arrays.asList("TEST_FLAG_1"))
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(conditions[0]))
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)

        def analysisRule2 = new BooleanAnalysisRule()
                .setFlags(Arrays.asList("TEST_FLAG_2"))
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(conditions[1]))
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)

        def analysisRule3 = new BooleanAnalysisRule()
                .setFlags(Arrays.asList("TEST_FLAG_3"))
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(conditions[2]))
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)

        analysisRuleRepository.saveAll(Arrays.asList(analysisRule, analysisRule2, analysisRule3))
        def analysisRules = analysisRuleRepository.findAll()


        def precondition = new PreconditionAnalysisRule()
                .setAnalysisRules(Arrays.asList(analysisRules[0], analysisRules[1]))
                .setConditions(Arrays.asList(conditions[3]))
                .setRuleType(AbstractAnalysisRule.RuleType.PRECONDITION)
                .setValue(true)
                .setAggregation(AbstractAnalysisRule.AggregationType.OR)


        analysisRuleRepository.save(precondition)
        precondition = (PreconditionAnalysisRule)analysisRuleRepository.findAll()[3]

        def rules = precondition.getAnalysisRules()

        rules.forEach(it -> it.setPreconditionId(precondition.getId()))
        analysisRuleRepository.saveAll(rules)

    }


    private List<AbstractCondition> createConditions() {
        def conditions = Arrays.asList(new ArrayCondition()
                .setValue(Arrays.asList("VALUE"))
                .setCriteria("rootNode.name")
                .setType(AbstractCondition.ConditionType.NOT_IN)
                .setDescription("TEST DESCRIPTION")
                .setMatchResult(true)
                .setName("TEST_NAME_1"),
                new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME_2"),
                new ArrayCondition()
                        .setValue(Arrays.asList("VALUE_2"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME_3"),
                new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("VALUE_2")
                        .setMatchResult(true)
                        .setName("TEST_NAME"))
        abstractConditionRepository.saveAll(conditions)
        return abstractConditionRepository.findAll()
    }
}
