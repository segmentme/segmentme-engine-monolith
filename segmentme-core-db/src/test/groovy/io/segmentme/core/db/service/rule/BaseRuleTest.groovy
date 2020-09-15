package io.segmentme.core.db.service.rule

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.common.BaseDatabaseTest
import io.segmentme.core.db.configuration.test.ResourceHolder
import io.segmentme.core.db.domain.rule.AbstractAnalysisRule
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule
import io.segmentme.core.db.dto.AnalysisResult
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository
import io.segmentme.core.db.repository.AbstractConditionRepository
import io.segmentme.core.db.service.ContextHolder
import io.segmentme.core.db.service.context.ContextPreprocessorServiceImpl
import io.segmentme.core.db.service.context.ContextSchemaResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Import
import org.springframework.core.io.Resource

@Import(ResourceHolder.class)
abstract class BaseRuleTest extends BaseDatabaseTest {

    @Value("classpath:rules/rules.json")
    protected Resource rulesJson

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected AnalysisService analysisService

    @Autowired
    protected ObjectMapper objectMapper

    @Autowired
    private ContextPreprocessorServiceImpl contextPreprocessorService

    @Autowired
    private ContextSchemaResolver contextSchemaResolver

    @Autowired
    protected AbstractConditionRepository abstractConditionRepository

    @Autowired
    protected AbstractAnalysisRuleRepository analysisRuleRepository

    private List<AbstractAnalysisRule<?>> rules

    private ContextHolder context

    def setup() {
        rules = objectMapper.readValue(rulesJson.getInputStream(), new TypeReference<List<AbstractAnalysisRule>>() {})
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        context = contextPreprocessorService.prepareContext(json, contextSchemaResolver.resolve(json))
    }

    def cleanup() {
        mongoTemplate.dropCollection("analysisRule")
        mongoTemplate.dropCollection("condition")
    }

    protected <T> T resultValue(String flagName, List<AnalysisResult> results) {
        return results.stream()
                .filter(it -> it.getNames().contains(flagName))
                .findFirst()
                .map(it -> it.getValue())
                .orElse(null) as T
    }

    def saveRule(String flag) {
        def rule = rules.stream()
                .filter(it -> it instanceof SimpleAnalysisRule)
                .map(it -> ((SimpleAnalysisRule) it))
                .filter(it -> it.flags.contains(flag))
                .findFirst()
                .orElse(null)

        def condition = abstractConditionRepository.saveAll(rule.getConditions())
        rule.setConditions(condition)
        return analysisRuleRepository.save(rule)
    }

    def getContext() {
        return this.context
    }
}
