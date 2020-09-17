package io.segmentme.core.service.rule

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule
import io.segmentme.core.db.dto.AnalysisResult
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository
import io.segmentme.core.db.service.ContextHolder
import io.segmentme.core.service.analysis.ContextPreprocessorServiceImpl
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
import io.segmentme.core.service.rule.AnalysisService
import lombok.extern.slf4j.Slf4j
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.core.io.Resource
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.util.stream.Collectors

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Import([ResourceHolder.class, TestConfig.class])
@AutoConfigureMockMvc
@ComponentScan("io.segmentme.core")
abstract class BaseRuleTest extends Specification {

    @SpringBootConfiguration
    public static class TestConfig {
    }

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected AnalysisService analysisService

    @Autowired
    protected ObjectMapper objectMapper

    @Autowired
    private ContextPreprocessorServiceImpl contextPreprocessorService

    @Autowired
    private ContextSchemaResolver contextSchemaResolver

    @SpringBean
    protected AbstractAnalysisRuleRepository analysisRuleRepository = Mock(AbstractAnalysisRuleRepository.class)

    private ContextHolder context

    def setup() {
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        context = contextPreprocessorService.prepareContext(json, contextSchemaResolver.resolve(json))
    }

    protected <T> T resultValue(String flagName, List<AnalysisResult> results) {
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


