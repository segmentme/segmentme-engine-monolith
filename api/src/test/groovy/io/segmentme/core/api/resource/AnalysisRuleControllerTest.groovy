package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.rule.RuleManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.IN
import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.BOOLEAN
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


    def "create rule: #rule"() {
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

        where:
        rule                                                                                                           | _
        ['value': true, 'ruleType': BOOLEAN, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))]  | _
    }
}
