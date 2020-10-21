package io.segmentme.core.api.resource

import com.fasterxml.jackson.databind.JsonNode
import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.StateRepository
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import io.segmentme.core.service.dto.analysis.state.StateDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.MediaType

import static java.util.UUID.randomUUID
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StateControllerTest extends BaseControllerTest {

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected StateRepository stateRepository;

    def cleanup() {
        stateRepository.deleteAll()
    }

    def 'create state with segment #segmentName'() {
        given:
        def stateDto = createState(getSegment(segmentName))
        def workspaceId = randomUUID().toString()
        and:
        def response = mockMvc.perform(auth(post("/state/workspace/${workspaceId}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(stateDto))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.name').value(stateDto.name))
                .andExpect(jsonPath('$.contextId').value(stateDto.contextId))
                .andExpect(jsonPath('$.integrationPoint').value(stateDto.integrationPoint))
                .andExpect(jsonPath('$.segment').isNotEmpty())
        where:
        segmentName                          | _
        "EMAIL_NOT_IN"                       | _
        "AGE_GT"                             | _
        "SECOND_PHONE_CONTAINS_ONLY"         | _
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | _
    }


    StateDto createState(SegmentDto segment) {
        return new StateDto()
                .setName(randomUUID().toString())
                .setContextId(randomUUID().toString())
                .setIntegrationPoint(randomUUID().toString())
                .setSegment(segment)
                .setValue(objectMapper.convertValue(Map.of("name", "test"), JsonNode.class))
    }
}
