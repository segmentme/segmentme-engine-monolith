package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.service.analysis.condition.ConditionManager
import io.segmentme.core.service.dto.analysis.conditions.AbstractConditionDto
import io.segmentme.core.service.dto.analysis.conditions.ArrayConditionDto
import io.segmentme.core.service.dto.analysis.conditions.SingleConditionDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import java.util.stream.Collectors
import java.util.stream.IntStream

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static java.util.UUID.randomUUID
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ConditionControllerTest extends BaseControllerTest {

    @Autowired
    private ConditionManager conditionManager

    @Autowired
    private AbstractConditionRepository abstractConditionRepository

    def cleanup() {
        abstractConditionRepository.deleteAll()
    }

    def "create condition: #condition"() {
        given:
        def request = (AbstractConditionDto) condition
        def contextId = randomUUID().toString()
        def response = mockMvc.perform(auth(post("/condition/context/${contextId}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(request))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        def strins = response.andReturn().response.contentAsString;
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
        fillCondition(new ArrayConditionDto(), [1, 2, 3], IN) | _
        fillCondition(new ArrayConditionDto(), ["VALUE"], CONTAINS_ONLY)             | _
        fillCondition(new ArrayConditionDto(), ["VALUE_1", "VALUE_2"], CONTAINS_ANY) | _
        fillCondition(new ArrayConditionDto(), [1], CONTAINS_ALL)                    | _
        fillCondition(new SingleConditionDto(), 1, LTE)                              | _
        fillCondition(new SingleConditionDto(), "1990-12-31", LT) | _
        fillCondition(new SingleConditionDto(), 500, GTE)                            | _
        fillCondition(new SingleConditionDto(), 413, GT)                             | _
        fillCondition(new SingleConditionDto(), 1, LTE)                              | _
    }

    def "create condition validation error: #condition"() {
        given:
        def request = (AbstractConditionDto) condition
        def contextId = randomUUID().toString()
        def response = mockMvc.perform(auth(post("/condition/context/${contextId}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(request))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isBadRequest()).andExpect(jsonPath('$.errorType').value("VALIDATION_ERROR"))
        where:
        condition                                                                         | _
        fillCondition(new ArrayConditionDto(), ['values': [1, 2, 3], 'type': IN])         | _
        fillCondition(new ArrayConditionDto(), ['values': ["ad"], 'type': CONTAINS_ONLY]) | _
        fillCondition(new ArrayConditionDto(), ['values': [2], 'type': CONTAINS_ONLY])    | _
    }


    def "delete condition"() {
        given:
        def id = createCondition(5)[0].id
        mockMvc.perform(auth(delete("/condition/${id}").contentType(MediaType.APPLICATION_JSON)))
        when:
        def conditions = abstractConditionRepository.findAll()
        then:
        conditions.size() == 4
    }


    private createCondition(int count) {
        return IntStream.range(0, count)
                .mapToObj(it -> fillCondition(new ArrayConditionDto(), [randomUUID().toString()], CONTAINS_ANY))
                .map(it -> conditionManager.create(it, randomUUID().toString()))
                .collect(Collectors.toList())
    }
}
