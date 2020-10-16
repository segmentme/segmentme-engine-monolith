package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.db.repository.SegmentRepository
import io.segmentme.core.service.dto.analysis.conditions.ArrayConditionDto
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import io.segmentme.core.service.rule.SegmentManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.IN
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule
import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SegmentControllerTest extends BaseControllerTest {

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

    def "success creation rule: #segment"() {
        given:
        def ruleToSave = fillRule(new SegmentDto(), segment)
        def response = mockMvc.perform(auth(post("/segment/${randomUUID().toString()}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status()
                .isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.aggregation').value(ruleToSave.aggregation.name()))
                .andExpect(jsonPath('$.embedded').value(ruleToSave.embedded))
                .andExpect(jsonPath('$.matchResult').value(ruleToSave.matchResult))
                .andExpect(jsonPath('$.name').value(ruleToSave.name))
                .andExpect(jsonPath('$.conditions', hasSize(ruleToSave.conditions.size())))
        where:
        segment                                                                                          | _
        ['matchResult': true, 'conditions': List.of(fillCondition(new ArrayConditionDto(), [true], IN))] | _

    }

    def "creation rule validation error: #segment"() {
        given:
        def ruleToSave = fillRule(new SegmentDto(), segment)
        ruleToSave.name = null
        def response = mockMvc.perform(auth(post("/segment/${randomUUID().toString()}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isBadRequest()).andExpect(jsonPath('$.errorType').value("VALIDATION_ERROR"))
        where:
        segment                                                                                                  | _
        ['value': true, 'name': '', 'conditions': List.of(fillCondition(new ArrayConditionDto(), ['type': IN]))] | _
    }
}
