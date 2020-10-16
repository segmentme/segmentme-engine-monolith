package io.segmentme.core.service.rule

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.domain.segment.Segment
import io.segmentme.core.db.domain.workpsace.Workspace
import io.segmentme.core.db.repository.SegmentRepository
import io.segmentme.core.service.analysis.ContextValueHolder
import io.segmentme.core.service.analysis.ContextValuesExtractorImpl
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource

import java.util.stream.Collectors

import static io.segmentme.core.service.helper.WorkspaceConfigurationHelper.defaultWorkspaceConfiguration

abstract class BaseRuleTest extends BaseTestWithContext {

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected AnalysisService analysisService

    @Autowired
    protected ObjectMapper objectMapper

    @Autowired
    private ContextValuesExtractorImpl contextValuesExtractor

    @Autowired
    private ContextSchemaResolver contextSchemaResolver

    @SpringBean
    protected SegmentRepository analysisRuleRepository = Mock(SegmentRepository.class)

    private ContextValueHolder context

    def setup() {
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        context = contextValuesExtractor.extractValues(json, contextSchemaResolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), json), defaultWorkspaceConfiguration())
    }

    protected static SegmentAnalysisResult resultValue(String name, List<SegmentAnalysisResult> results) {
        return results.stream()
                .filter(it -> it.getName().contains(name))
                .findFirst()
                .orElse(null)
    }

    def getContext() {
        return this.context
    }

    protected getSegment(String segmentName) {
        return resourceHolder.getSegments().stream().filter(it -> match(it, segmentName)).collect(Collectors.toList())
    }

    static boolean match(Segment segment, String segmentName) {
        return segment.getName().equalsIgnoreCase(segmentName)
    }
}


