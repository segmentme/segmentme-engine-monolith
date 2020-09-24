package io.segmentme.core.api.resource

import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.service.condition.ConditionManager
import io.segmentme.core.service.dto.component.AbstractConditionDto
import io.segmentme.core.service.dto.component.ArrayConditionDto
import io.segmentme.core.service.dto.component.GroupConditionDto
import io.segmentme.core.service.dto.component.SingleConditionDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType

import java.util.stream.Collectors
import java.util.stream.IntStream

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.*
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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

    def "create condition validation error: #condition"() {
        given:
        def request = (AbstractConditionDto) condition
        def contextId = randomUUID().toString()
        def response = mockMvc.perform(post("/condition/${contextId}")
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


    def "get condition by contextId: #contextId count should be #count"() {
        given:
        conditionManager.createAll(conditions, contextId)
        def response = mockMvc.perform(get("/condition/${contextId}").contentType(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isOk()).andExpect(jsonPath('$.*', hasSize(count)))
        where:
        conditions                                                                                                                                                                                    | contextId               | count
        [fillCondition(new ArrayConditionDto(), [1, 2, 3], IN)]                                                                                                                                       | randomUUID().toString() | 1
        [fillCondition(new ArrayConditionDto(), [2], CONTAINS_ONLY), fillCondition(new ArrayConditionDto(), [2], CONTAINS_ANY)]                                                                       | randomUUID().toString() | 2
        [fillCondition(new GroupConditionDto(), ['values': [2], 'type': GROUP, 'conditions': [fillCondition(new ArrayConditionDto(), ['values': ["ad"], 'type': CONTAINS_ONLY, 'embedded': true])]])] | randomUUID().toString() | 1
        [
                fillCondition(new ArrayConditionDto(), [1, 2, 3], IN),
                fillCondition(new ArrayConditionDto(), [1, 2, 3], CONTAINS_ANY),
                fillCondition(new ArrayConditionDto(), [1, 2, 3], CONTAINS_ONLY),
                fillCondition(new ArrayConditionDto(), ['values': [1, 2, 3], 'type': IN, 'embedded': true]),
                fillCondition(new ArrayConditionDto(), ['values': [1, 2, 3], 'type': IN, 'embedded': true])
        ]                                                                                                                                                                                             | randomUUID().toString() | 3
    }

    def "delete condition"() {
        given:
        def id = createCondition(5)[0].id
        mockMvc.perform(delete("/condition/${id}").contentType(MediaType.APPLICATION_JSON))
        when:
        def conditions = abstractConditionRepository.findAll()
        then:
        conditions.size() == 4
    }

    def "delete condition with embedded condition"() {
        given:
        def conditions = [fillCondition(new GroupConditionDto(),
                [fillCondition(new GroupConditionDto(),
                        [fillCondition(new GroupConditionDto(),
                                [
                                        fillCondition(new GroupConditionDto(), [
                                                fillCondition(new ArrayConditionDto(), [1], IN, true),
                                                fillCondition(new ArrayConditionDto(), [2], IN, true)
                                        ], GROUP, false),
                                        fillCondition(new GroupConditionDto(), [
                                                fillCondition(new ArrayConditionDto(), [3], IN, false),
                                                fillCondition(new ArrayConditionDto(), [4], IN, false)
                                        ], GROUP, true)
                                ],
                                GROUP, true)],
                        GROUP, true)],
                GROUP, false)]
        def createdConditions = conditionManager.createAll(conditions, "32123")
        mockMvc.perform(delete("/condition/${createdConditions[0].id}").contentType(MediaType.APPLICATION_JSON))
        when:
        def existedCondition = abstractConditionRepository.findAll()
        then:
        existedCondition.size() == 3
    }

    private createCondition(int count) {
        return IntStream.range(0, count)
                .mapToObj(it -> fillCondition(new ArrayConditionDto(), [randomUUID().toString()], CONTAINS_ANY))
                .map(it -> conditionManager.create(it, randomUUID().toString()))
                .collect(Collectors.toList())
    }
}
