package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.rule.*;
import io.segmentme.core.service.dto.rule.*;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class RuleConverter {

    public AbstractAnalysisRule<?> of(AbstractAnalysisRuleDto<?> source, String contextId, String integrationPointKey) {
        return convertToEntity(source, contextId, integrationPointKey);
    }

    public AbstractAnalysisRuleDto<?> of(AbstractAnalysisRule<?> source) {
        return convertToDto(source);
    }

    private AbstractAnalysisRuleDto<?> convertToDto(AbstractAnalysisRule<?> source) {
        return switch (source.getRuleType()) {
            case BOOLEAN -> convertToDto(new BooleanAnalysisRuleDto(), (SimpleAnalysisRule<?>) source);
            case JSON -> convertToDto(new JsonAnalysisRuleDto(), (SimpleAnalysisRule<?>) source);
            case PRECONDITION -> convertToDto(new PreconditionAnalysisRuleDto(), (PreconditionAnalysisRule) source);
            default -> throw new IllegalArgumentException("Unknown rule type " + source.getRuleType());
        };
    }

    private AbstractAnalysisRule<?> convertToEntity(AbstractAnalysisRuleDto<?> source, String contextId, String integrationPointKey) {
        return switch (source.getRuleType()) {
            case BOOLEAN -> convertToEntity(new BooleanAnalysisRule(), (SimpleAnalysisRuleDto<?>) source, contextId, integrationPointKey);
            case JSON -> convertToEntity(new JsonAnalysisRule(), (SimpleAnalysisRuleDto<?>) source, contextId, integrationPointKey);
            case PRECONDITION -> convertToEntity(new PreconditionAnalysisRule(), (PreconditionAnalysisRuleDto) source, contextId, integrationPointKey);
            default -> throw new IllegalArgumentException("Unknown rule type " + source.getRuleType());
        };
    }

    private AbstractAnalysisRuleDto<?> convertToDto(SimpleAnalysisRuleDto<?> target, SimpleAnalysisRule<?> source) {
        return fillRule(target.setFlags(source.getFlags()), source);
    }

    private AbstractAnalysisRuleDto<?> convertToDto(PreconditionAnalysisRuleDto target, PreconditionAnalysisRule source) {
        target.setAnalysisRules(source.getAnalysisRules().stream().map(RuleConverter::of).collect(Collectors.toList()))
                .setAggregation(source.getAggregation())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()));
        return fillRule(target, source);
    }

    private AbstractAnalysisRule<?> convertToEntity(PreconditionAnalysisRule target, PreconditionAnalysisRuleDto source, String contextId, String integrationPointKey) {
        target.setAnalysisRules(source.getAnalysisRules().stream().map(it -> of(it, contextId, integrationPointKey)).collect(Collectors.toList()))
                .setAggregation(source.getAggregation())
                .setConditions(source.getConditions().stream().map(it -> ConditionConverter.of(it, contextId)).collect(Collectors.toList()));
        return fillRule(target, source, contextId, integrationPointKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractAnalysisRule<?> convertToEntity(SimpleAnalysisRule target, SimpleAnalysisRuleDto source, String contextId, String integrationPointKey) {
        return fillRule(target.setFlags(source.getFlags()), source, contextId, integrationPointKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractAnalysisRule<?> fillRule(AbstractAnalysisRule target, AbstractAnalysisRuleDto<?> source, String contextId, String integrationPointKey) {
        target.setId(source.getId());
        return target.setAggregation(source.getAggregation())
                .setValue(source.getValue())
                .setEmbedded(source.isEmbedded())
                .setConditions(source.getConditions().stream().map(it -> ConditionConverter.of(it, contextId)).collect(Collectors.toList()))
                .setContextId(contextId)
                .setIntegrationPointKey(integrationPointKey)
                .setRuleType(source.getRuleType());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private AbstractAnalysisRuleDto<?> fillRule(AbstractAnalysisRuleDto target, AbstractAnalysisRule<?> source) {
        return target.setId(source.getId())
                .setEmbedded(source.isEmbedded())
                .setAggregation(source.getAggregation())
                .setValue(source.getValue())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()))
                .setRuleType(source.getRuleType());
    }
}
