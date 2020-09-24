package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.rule.RuleManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.IN
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.BOOLEAN
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.PRECONDITION
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.rule.RuleManagerTest.createRule
import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AnalysisRuleControllerTest extends BaseControllerTest {

    @Autowired
    private RuleManager ruleManager

    @Autowired
    private AbstractAnalysisRuleRepository analysisRuleRepository

    def "success creation rule: #rule"() {
        given:
        def ruleToSave = createRule(rule)
        def response = mockMvc.perform(post("/rule/${randomUUID().toString()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status()
                .isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.ruleType').value(ruleToSave.ruleType.name()))
                .andExpect(jsonPath('$.aggregation').value(ruleToSave.aggregation.name()))
                .andExpect(jsonPath('$.embedded').value(ruleToSave.embedded))
                .andExpect(jsonPath('$.value').value(ruleToSave.value))
                .andExpect(jsonPath('$.flags', hasSize(ruleToSave.flags.size())))
                .andExpect(jsonPath('$.conditions', hasSize(ruleToSave.conditions.size())))
        where:
        rule                                                                                                            | _
        ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))] | _

    }

    def "success creation precondition rule: #rule"() {
        given:
        def ruleToSave = createRule(rule)
        def response = mockMvc.perform(post("/rule/${randomUUID().toString()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status()
                .isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.ruleType').value(ruleToSave.ruleType.name()))
                .andExpect(jsonPath('$.aggregation').value(ruleToSave.aggregation.name()))
                .andExpect(jsonPath('$.embedded').value(ruleToSave.embedded))
                .andExpect(jsonPath('$.value').value(ruleToSave.value))
                .andExpect(jsonPath('$.conditions', hasSize(ruleToSave.conditions.size())))
                .andExpect(jsonPath('$.analysisRules', hasSize(ruleToSave.analysisRules.size())))
        where:
        rule                                                                                                                                                    | _
        ['value'        : true, 'ruleType': PRECONDITION, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN)),
         'analysisRules': List.of(createRule(['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))]))] | _
    }
}
