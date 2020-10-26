package io.segmentme.core.api.resource

import com.fasterxml.jackson.databind.JsonNode
import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.api.error.dto.ErrorType
import io.segmentme.core.api.security.SecurityService
import io.segmentme.core.db.repository.StateRepository
import io.segmentme.core.service.analysis.state.StateManager
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import io.segmentme.core.service.dto.analysis.state.StateDto
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource

import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.hasItem
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StateControllerTest extends BaseControllerTest {

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected StateRepository stateRepository

    @Autowired
    protected StateManager stateManager

    @SpringBean
    private SecurityService securityService = Mock(SecurityService.class)

    def setup(){
        securityService.isValidIntegrationPointKey(_, _) >> true
    }

    def cleanup() {
        stateRepository.deleteAll()
    }

    def 'create state with segment #segmentName'() {
        given:
        def stateDto = createState(getSegment(segmentName))
        and:
        def response = sendRequest(post("/state"), stateDto)
        expect:
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.name').value(stateDto.name))
                .andExpect(jsonPath('$.integrationPointKey').value(stateDto.integrationPointKey))
                .andExpect(jsonPath('$.segment').isNotEmpty())
        where:
        segmentName                          | _
        "EMAIL_NOT_IN"                       | _
        "AGE_GT"                             | _
        "SECOND_PHONE_CONTAINS_ONLY"         | _
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | _
    }


    def 'create state with segment #segmentName - validation fail'() {
        given:
        def stateDto = createState(null)
        stateDto.setName(null).setValue(null)
        and:
        def response = sendRequest(post("/state"), stateDto)
        expect:
        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath('$.errorType').value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath('$.errorType').value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath('$.fieldErrors[*].field', hasItem("segment")))
                .andExpect(jsonPath('$.fieldErrors[*].field', hasItem("name")))
        where:
        segmentName                          | _
        "SECOND_PHONE_CONTAINS_ONLY"         | _
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | _
    }

    def 'update state for workplace'() {
        given:
        def state = stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY_SEGMENT")))
        state.setName("UPDATED_NAME")
        when:
        def response = sendRequest(put("/state/${state.id}"), state)
        then:
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.name').value("UPDATED_NAME"))
                .andExpect(jsonPath('$.integrationPointKey').value(state.integrationPointKey))
                .andExpect(jsonPath('$.segment').isNotEmpty())
    }

    def 'update state for workplace - validation error'() {
        given:
        def state = stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY_SEGMENT")))
        state.setName(null)
        when:
        def response = sendRequest(put("/state/${state.id}"), state)
        then:
        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath('$.errorType').value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath('$.errorType').value(ErrorType.VALIDATION_ERROR.name()))
                .andExpect(jsonPath('$.fieldErrors[*].field', hasItem("name")))
    }

    def 'get states for workplace'() {
        given:
        def firstState = stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY")))
        stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY_SEGMENT")).setIntegrationPointKey(firstState.integrationPointKey))
        when:
        def response = sendRequest(get("/state/integrationPointKey/${firstState.integrationPointKey}"))
        then:
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath('$[*]', hasSize(2)))
                .andExpect(jsonPath('$[*].id').isNotEmpty())
                .andExpect(jsonPath('$[*].name').isNotEmpty())
                .andExpect(jsonPath('$[*].segment').isNotEmpty())
    }

    def 'delete state'() {
        given:
        def firstSegment = stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY")))
        def secondSegment = stateManager.create(createState(getSegment("SECOND_PHONE_CONTAINS_ONLY_SEGMENT")))
        when:
        def response = sendRequest(delete("/state/${secondSegment.id}"))
        then:
        response.andExpect(status().isOk()).andDo(print())
        def segments = stateRepository.findAll()
        segments.size() == 1
        segments.get(0).id == firstSegment.id
    }


    private StateDto createState(SegmentDto segment) {
        return new StateDto()
                .setName(randomUUID().toString())
                .setIntegrationPointKey(randomUUID().toString())
                .setSegment(segment)
                .setValue(objectMapper.convertValue(Map.of("name", "test"), JsonNode.class))
    }
}
