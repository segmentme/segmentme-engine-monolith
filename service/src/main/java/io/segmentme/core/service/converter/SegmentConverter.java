package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class SegmentConverter {

    public Segment of(SegmentDto source, String contextId, String integrationPointKey) {
        return (Segment) new Segment()
                .setContextId(contextId)
                .setIntegrationPointKey(integrationPointKey)
                .setName(source.getName())
                .setAggregation(source.getAggregation())
                .setMatchResult(source.isMatchResult())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()))
                .setId(source.getId());
    }

    public SegmentDto of(Segment source) {
        return new SegmentDto()
                .setId(source.getId())
                .setName(source.getName())
                .setAggregation(source.getAggregation())
                .setMatchResult(source.isMatchResult())
                .setConditions(source.getConditions().stream().map(ConditionConverter::of).collect(Collectors.toList()));
    }
}
