package io.segmentme.core.api.resource

import com.fasterxml.jackson.databind.JsonNode
import io.segmentme.core.api.common.BaseControllerTest
import io.segmentme.core.api.security.ContextSchemaSecurityService
import io.segmentme.core.db.domain.workpsace.Workspace

import io.segmentme.core.db.repository.SegmentRepository
import io.segmentme.core.service.analysis.ContextValueHolder
import io.segmentme.core.service.analysis.ContextValuesExtractorImpl
import io.segmentme.core.service.analysis.segment.SegmentManager
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
import io.segmentme.core.service.dto.analysis.conditions.ArrayConditionDto
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.MediaType

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.IN
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule
import static io.segmentme.core.service.helper.WorkspaceConfigurationHelper.defaultWorkspaceConfiguration
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

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    private ContextValuesExtractorImpl contextValuesExtractor

    @Autowired
    private ContextSchemaResolver contextSchemaResolver

    @Autowired
    protected ResourceHolder resourceHolder

    private ContextValueHolder context

    @SpringBean
    protected ContextSchemaSecurityService contextSchemaSecurityService = Mock(ContextSchemaSecurityService.class)

    def setup(){
        contextSchemaSecurityService.isManagedSchema(_ as String) >> true
    }

    def cleanup() {
        analysisRuleRepository.deleteAll()
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        context = contextValuesExtractor.extractValues(json, contextSchemaResolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), json), defaultWorkspaceConfiguration())
    }

    def "save segment with segmentCondition with name: #name"() {
        given:
        def ruleToSave = getSegment(name)
        def response = mockMvc.perform(auth(post("/segment/${randomUUID().toString()}"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializeToJson(ruleToSave))
                .accept(MediaType.APPLICATION_JSON))
        expect:
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath('$.id').isNotEmpty())
                .andExpect(jsonPath('$.aggregation').value(ruleToSave.aggregation.name()))
                .andExpect(jsonPath('$.matchResult').value(ruleToSave.matchResult))
                .andExpect(jsonPath('$.name').value(ruleToSave.name))
                .andExpect(jsonPath('$.conditions', hasSize(ruleToSave.conditions.size())))
        where:
        name                                 | _
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | _
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
