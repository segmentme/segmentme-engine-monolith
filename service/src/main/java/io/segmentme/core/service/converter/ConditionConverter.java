package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.condition.*;
import io.segmentme.core.service.dto.analysis.component.*;
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
            case SEGMENT -> convertSegmentConditionDto((SegmentCondition) source);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    private AbstractCondition convertToEntity(AbstractConditionDto source, String contextId) {
        return switch (source.getType()) {
            case CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY, IN -> convertToEntity(new ArrayCondition(), (ArrayConditionDto) source, contextId);
            case LTE, LT, GTE, GT -> convertToEntity(new SingleCondition(), (SingleConditionDto) source, contextId);
            case RANGE -> convertToEntity(new RangeCondition(), (RangeConditionDto) source, contextId);
            case SEGMENT -> convertToSegmentConditionEntity((SegmentConditionDto) source, contextId);
            default -> throw new IllegalArgumentException("Unknown condition type " + source.getType());
        };
    }

    private AbstractCondition convertToSegmentConditionEntity(SegmentConditionDto source, String contextId) {
        SegmentCondition target = new SegmentCondition().setSegment(SegmentConverter.of(source.getSegment(), contextId, null));
        return fillAbstractCondition(target, source, contextId);
    }

    private AbstractConditionDto convertSegmentConditionDto(SegmentCondition source) {
        SegmentConditionDto target = new SegmentConditionDto();
        target.setSegment(SegmentConverter.of(source.getSegment()));
        return fillAbstractCondition(target, source);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractCondition convertToEntity(SimpleCondition target, SimpleConditionDto source, String contextId) {
        target.setValue(source.getValue()).setNullValid(source.isNullValid());
        target.setCriteria(source.getCriteria());
        return fillAbstractCondition(target, source, contextId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static AbstractConditionDto convertToDto(SimpleConditionDto target, SimpleCondition source) {
        target.setValue(source.getValue()).setNullValid(source.isNullValid());
        target.setCriteria(source.getCriteria());
        return fillAbstractCondition(target, source);
    }

    private static AbstractConditionDto fillAbstractCondition(AbstractConditionDto target, AbstractCondition source) {
        target.setId(source.getId());
        return target.setMatchResult(source.isMatchResult())
                .setEmbedded(source.isEmbedded())
                .setDescription(source.getDescription())
                .setName(source.getName())
                .setType(source.getType());
    }

    private static AbstractCondition fillAbstractCondition(AbstractCondition target, AbstractConditionDto source, String contextId) {
        target.setId(source.getId());
        return target.setMatchResult(source.isMatchResult())
                .setEmbedded(source.isEmbedded())
                .setDescription(source.getDescription())
                .setName(source.getName())
                .setType(source.getType())
                .setContextId(contextId);
    }
}
