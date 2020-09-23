package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.service.dto.component.AbstractConditionDto
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.dto.component.SingleConditionDto
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ConditionControllerTest extends BaseControllerTest {

    def "create condition: #condition"() {
        given:
        def request = (AbstractConditionDto) condition
        def contextId = UUID.randomUUID().toString()
        def response = mockMvc.perform(post("/condition/${contextId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(request))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status()
                .isOk())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.name').value(request.name))
                .andExpect(jsonPath('$.criteria').value(request.criteria))
                .andExpect(jsonPath('$.description').value(request.description))
                .andExpect(jsonPath('$.type').value(request.type.name()))
                .andExpect(jsonPath('$.matchResult').value(request.matchResult))
                .andExpect(jsonPath('$.embedded').value(request.embedded))
                .andExpect(jsonPath('$.nullValid').value(request.nullValid))
        where:
        condition                                                                    | _
        fillCondition(new ArrayConditionDto(), [1, 2, 3], IN)                        | _
        fillCondition(new ArrayConditionDto(), ["VALUE"], CONTAINS_ONLY)             | _
        fillCondition(new ArrayConditionDto(), ["VALUE_1", "VALUE_2"], CONTAINS_ANY) | _
        fillCondition(new ArrayConditionDto(), [1], CONTAINS_ALL)                    | _
        fillCondition(new SingleConditionDto(), 1, LTE)                              | _
        fillCondition(new SingleConditionDto(), "1990-12-31", LT)                    | _
        fillCondition(new SingleConditionDto(), 500, GTE)                            | _
        fillCondition(new SingleConditionDto(), 413, GT)                             | _
        fillCondition(new SingleConditionDto(), 1, LTE)                              | _
    }
}
