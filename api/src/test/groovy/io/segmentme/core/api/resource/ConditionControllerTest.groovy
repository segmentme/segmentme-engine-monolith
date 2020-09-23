package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.service.dto.component.ArrayConditionDto
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ConditionControllerTest extends BaseControllerTest {

    def "create condition: #condition"() {
        given:
        def contextId = UUID.randomUUID().toString()
        def response = mockMvc.perform(post("/condition/${contextId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(condition))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        def string = response.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        string != null
        where:
        condition                                                                    | _
        fillCondition(new ArrayConditionDto(), [1, 2, 3], IN)                        | _
    }
}
