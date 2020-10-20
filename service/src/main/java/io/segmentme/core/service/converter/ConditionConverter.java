package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.condition.*;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.dto.analysis.conditions.*;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConditionConverter {

    public AbstractCondition of(AbstractConditionDto source) {
        return convertToEntity(source);
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

    private AbstractCondition convertToEntity(AbstractConditionDto source) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToEntity(new ArrayCondition(), (ArrayConditionDto) source);
            case LTE, LT, GTE, GT -> convertToEntity(new SingleCondition(), (SingleConditionDto) source);
            case RANGE -> convertToEntity(new RangeCondition(), (RangeConditionDto) source);
            case SEGMENT -> convertToEntity(new SegmentCondition(), (SegmentConditionDto) source);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractCondition convertToEntity(SimpleCondition target, SimpleConditionDto source) {
        Object conditionValue = source.getValue();

        if (conditionValue instanceof SegmentDto) {
            SegmentDto value = (SegmentDto) source.getValue();
            conditionValue = SegmentConverter.of(value, null, null);
        }

        return target.setValue(conditionValue)
                .setNullValid(source.isNullValid())
                .setCriteria(source.getCriteria())
                .setMatchResult(source.isMatchResult())
                .setType(source.getType())
                .setHash(String.valueOf(target.hashCode()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractConditionDto convertToDto(SimpleConditionDto target, SimpleCondition source) {
        Object conditionValue = source.getValue();

        if (conditionValue instanceof Segment) {
            Segment value = (Segment) source.getValue();
            conditionValue = SegmentConverter.of(value);
        }

        return target.setValue(conditionValue)
                .setNullValid(source.isNullValid())
                .setCriteria(source.getCriteria())
                .setMatchResult(source.isMatchResult())
                .setType(source.getType())
                .setHash(source.getHash());
    }
}
