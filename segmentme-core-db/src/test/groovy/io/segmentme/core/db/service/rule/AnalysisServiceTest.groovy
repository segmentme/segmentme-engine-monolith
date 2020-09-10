package io.segmentme.core.db.service.rule

import io.segmentme.core.db.common.BaseDatabaseTest
import io.segmentme.core.db.domain.condition.AbstractCondition
import io.segmentme.core.db.domain.condition.ArrayCondition
import io.segmentme.core.db.domain.context.AnalysisContextSchema
import io.segmentme.core.db.domain.context.SchemaNode
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.db.repository.PreconditionAnalysisRuleRepository
import io.segmentme.core.db.repository.SimpleAnalysisRuleRepository
import org.springframework.beans.factory.annotation.Autowired

class AnalysisServiceTest extends BaseDatabaseTest {

    @Autowired
    private SimpleAnalysisRuleRepository simpleAnalysisRuleRepository
    @Autowired
    private PreconditionAnalysisRuleRepository preconditionAnalysisRuleRepository
    @Autowired
    private AbstractConditionRepository abstractConditionRepository
    @Autowired
    private AnalysisService analysisRuleService;

    def 'cascade save: Success'() {
        setup:
        def date = prepareDate()
        when:
        preconditionAnalysisRuleRepository.save(date)
        then:
        def result = analysisRuleService.analyze(new AnalysisContextSchema().setRootNode(new SchemaNode().setName("VALUE")))

        result != null
        result.size() == 3
        result.stream().filter(it -> it.getNames().contains("TEST_FLAG_1")).findFirst().isPresent()
        result.stream().filter(it -> it.getNames().contains("TEST_FLAG_2")).findFirst().isPresent()
        result.stream().filter(it -> it.getNames().contains("TEST_FLAG_3")).findFirst().isPresent()
    }

    private PreconditionAnalysisRule prepareDate() {
        def analysisRule = (BooleanAnalysisRule) new BooleanAnalysisRule()
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.NOT_IN)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME_1"))
                )
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)

        analysisRule.setFlags(Arrays.asList("TEST_FLAG_1"))


        def analysisRule2 = (BooleanAnalysisRule) new BooleanAnalysisRule()
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME_2"))
                )
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)
        analysisRule2.setFlags(Arrays.asList("TEST_FLAG_2"))


        def analysisRule3 = (BooleanAnalysisRule) new BooleanAnalysisRule()
                .setAggregation(AbstractAnalysisRule.AggregationType.AND)
                .setConditions(Arrays.asList(new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("TEST DESCRIPTION")
                        .setMatchResult(true)
                        .setName("TEST_NAME_3"))
                )
                .setRuleType(AbstractAnalysisRule.RuleType.BOOLEAN)
                .setValue(true)
        analysisRule3.setFlags(Arrays.asList("TEST_FLAG_3"))

        simpleAnalysisRuleRepository.save(analysisRule3)

        return new PreconditionAnalysisRule()
                .setAnalysisRules(Arrays.asList(analysisRule, analysisRule2))
                .setConditions(Arrays.asList(new ArrayCondition()
                        .setValue(Arrays.asList("VALUE"))
                        .setCriteria("rootNode.name")
                        .setType(AbstractCondition.ConditionType.IN)
                        .setDescription("VALUE_2")
                        .setMatchResult(true)
                        .setName("TEST_NAME")))
                .setRuleType(AbstractAnalysisRule.RuleType.PRECONDITION)
                .setValue(true)
                .setAggregation(AbstractAnalysisRule.AggregationType.OR)
    }
}
