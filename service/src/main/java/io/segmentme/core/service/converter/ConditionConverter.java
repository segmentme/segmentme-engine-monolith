package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.condition.*;
import io.segmentme.core.service.dto.component.*;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ConditionConverter {

    public AbstractCondition<?> of(AbstractConditionDto<?> source, String contextId) {
        return convertToEntity(source, contextId);
    }

    public AbstractConditionDto<?> of(AbstractCondition<?> source) {
        return convertToDto(source);
    }

    private AbstractConditionDto<?> convertToDto(AbstractCondition<?> source) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToDto(new ArrayConditionDto(), (ArrayCondition) source);
            case LTE, LT, GTE, GT -> convertToDto(new SingleConditionDto(), (SingleCondition) source);
            case RANGE -> convertToDto(new RangeConditionDto(), (RangeCondition) source);
            case GROUP -> convertToGroupConditionDto((GroupCondition) source);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    private AbstractCondition<?> convertToEntity(AbstractConditionDto<?> source, String contextId) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToEntity(new ArrayCondition(), (ArrayConditionDto) source, contextId);
            case LTE, LT, GTE, GT -> convertToEntity(new SingleCondition(), (SingleConditionDto) source, contextId);
            case RANGE -> convertToEntity(new RangeCondition(), (RangeConditionDto) source, contextId);
            case GROUP -> convertToGroupConditionEntity((GroupConditionDto) source, contextId);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    private AbstractCondition<?> convertToGroupConditionEntity(GroupConditionDto source, String contextId) {
        GroupCondition target = new GroupCondition();
        List<AbstractCondition<?>> conditions = source.getConditions().stream().map(it -> ConditionConverter.of(it, contextId)).collect(Collectors.toList());
        target.setAggregation(source.getAggregation()).setConditions(conditions);
        return fillAbstractCondition(target, source, contextId);
    }

    private AbstractConditionDto<?> convertToGroupConditionDto(GroupCondition source) {
        GroupConditionDto target = new GroupConditionDto();
        List<AbstractConditionDto<?>> conditions = source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList());
        target.setAggregation(source.getAggregation()).setConditions(conditions);
        return fillAbstractCondition(target, source);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractCondition<?> convertToEntity(SimpleCondition target, SimpleConditionDto source, String contextId) {
        target.setValue(source.getValue()).setNullValid(source.isNullValid());
        return fillAbstractCondition(target, source, contextId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractConditionDto<?> convertToDto(SimpleConditionDto target, SimpleCondition source) {
        target.setValue(source.getValue()).setNullValid(source.isNullValid());
        return fillAbstractCondition(target, source);
    }

    private static AbstractConditionDto<?> fillAbstractCondition(AbstractConditionDto<?> target, AbstractCondition<?> source) {
        target.setId(source.getId());
        return target.setMatchResult(source.isMatchResult())
                .setEmbedded(source.isEmbedded())
                .setCriteria(source.getCriteria())
                .setDescription(source.getDescription())
                .setName(source.getName())
                .setType(source.getType());
    }

    private static AbstractCondition<?> fillAbstractCondition(AbstractCondition<?> target, AbstractConditionDto<?> source, String contextId) {
        target.setId(source.getId());
        return target.setMatchResult(source.isMatchResult())
                .setEmbedded(source.isEmbedded())
                .setCriteria(source.getCriteria())
                .setDescription(source.getDescription())
                .setName(source.getName())
                .setType(source.getType())
                .setContextId(contextId);
    }
}
