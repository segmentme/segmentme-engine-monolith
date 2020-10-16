package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.SegmentRepository
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.rule.SegmentManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.IN
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.BOOLEAN
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.PRECONDITION
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.rule.RuleManagerTest.createRule
import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AnalysisRuleControllerTest extends BaseControllerTest {

    @Autowired
    private SegmentManager ruleManager

    @Autowired
    private SegmentRepository analysisRuleRepository

    @Autowired
    private AbstractConditionRepository abstractConditionRepository

    def cleanup() {
        abstractConditionRepository.deleteAll()
        analysisRuleRepository.deleteAll()
    }


    def "success creation rule: #rule"() {
        given:
        def ruleToSave = createRule(rule)
        def response = mockMvc.perform(auth(post("/rule/${randomUUID().toString()}"))
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
        def response = mockMvc.perform(auth(post("/rule/${randomUUID().toString()}"))
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

    def "creation rule validation error: #rule"() {
        given:
        def ruleToSave = createRule(rule)
        def response = mockMvc.perform(auth(post("/rule/${randomUUID().toString()}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isBadRequest()).andExpect(jsonPath('$.errorType').value("VALIDATION_ERROR"))
        where:
        rule                                                                                                                           | _
        ['value': true, 'ruleType': PRECONDITION, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))]           | _
        ['value': true, 'flags': [], 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), ['type': IN]))] | _
    }


    def "delete rules with embedded rules"() {
        given:
        def rules = ['value'        : true, 'ruleType': PRECONDITION, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN)),
                     'analysisRules': List.of(
                             createRule(['value': true, 'embedded': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN, true))]),
                             createRule(['value': true, 'embedded': false, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))])
                     )]
        def savedRule = ruleManager.save(createRule(rules), randomUUID().toString(), randomUUID().toString())
        mockMvc.perform(auth(delete("/rule/${savedRule.id}"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        when:
        def existedRules = analysisRuleRepository.findAll()
        def existedCondition = abstractConditionRepository.findAll()
        then:
        existedRules.size() == 1
        existedCondition.size() == 2
    }
}
