package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.condition.*;
import io.segmentme.core.service.dto.analysis.conditions.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConditionConverter {

    public AbstractCondition of(AbstractConditionDto source, String contextId) {
        return convertToEntity(source, contextId);
    }

    public AbstractConditionDto of(AbstractCondition source) {
        return convertToDto(source);
    }

    private AbstractConditionDto convertToDto(AbstractCondition source) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToDto(new ArrayConditionDto(), (ArrayCondition) source);
            case LTE, LT, GTE, GT -> convertToDto(new SingleConditionDto(), (SingleCondition) source);
            case RANGE -> convertToDto(new RangeConditionDto(), (RangeCondition) source);
            case SEGMENT -> convertToDto(new SegmentConditionDto(), (SegmentCondition) source);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    private AbstractCondition convertToEntity(AbstractConditionDto source, String contextId) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToEntity(new ArrayCondition(), (ArrayConditionDto) source, contextId);
            case LTE, LT, GTE, GT -> convertToEntity(new SingleCondition(), (SingleConditionDto) source, contextId);
            case RANGE -> convertToEntity(new RangeCondition(), (RangeConditionDto) source, contextId);
            case SEGMENT -> convertToEntity(new SegmentCondition(), (SegmentConditionDto) source, contextId);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractCondition convertToEntity(SimpleCondition target, SimpleConditionDto source, String contextId) {
        return target.setValue(source.getValue())
                .setNullValid(source.isNullValid())
                .setCriteria(source.getCriteria())
                .setMatchResult(source.isMatchResult())
                .setDescription(source.getDescription())
                .setType(source.getType());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractConditionDto convertToDto(SimpleConditionDto target, SimpleCondition source) {
        return target.setValue(source.getValue()).setNullValid(source.isNullValid())
                .setCriteria(source.getCriteria())
                .setMatchResult(source.isMatchResult())
                .setDescription(source.getDescription())
                .setType(source.getType());
    }
}
