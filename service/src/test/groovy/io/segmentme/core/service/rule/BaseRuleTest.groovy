package io.segmentme.core.service.rule

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule

import io.segmentme.core.db.domain.workpsace.Workspace
import io.segmentme.core.service.dto.rule.SegmentAnalysisResult
import io.segmentme.core.db.repository.SegmentRepository
import io.segmentme.core.service.analysis.ContextValueHolder
import io.segmentme.core.service.analysis.ContextValuesExtractorImpl
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
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

    protected <T> T resultValue(String flagName, List<SegmentAnalysisResult> results) {
        return results.stream()
                .filter(it -> it.getNames().contains(flagName))
                .findFirst()
                .map(it -> it.getValue())
                .orElse(null) as T
    }

    def getContext() {
        return this.context
    }

    protected getRule(String flagName) {
        return resourceHolder.getRuleSchema().stream().filter(it -> match(it, flagName)).collect(Collectors.toList())
    }

    boolean match(AbstractAnalysisRule rule, String flagName) {
        boolean isExist = false
        if (rule instanceof SimpleAnalysisRule) {
            isExist = ((SimpleAnalysisRule) rule).getFlags().contains(flagName)
        } else if (rule.getRuleType() == AbstractAnalysisRule.RuleType.PRECONDITION && !isExist) {
            isExist = ((PreconditionAnalysisRule) rule).getAnalysisRules().stream().filter(it -> match(it, flagName)).findFirst().isPresent()
        }
        return isExist
    }
}


