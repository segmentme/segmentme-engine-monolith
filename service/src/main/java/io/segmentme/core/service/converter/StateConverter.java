package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.service.segment.SegmentService;
import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import io.segmentme.core.service.dto.analysis.state.StateDto;
import io.segmentme.core.service.exception.SegmentManagerException;
import io.segmentme.core.service.exception.error.SegmentMangerErrors;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateConverter {

    private final SegmentService segmentService;

    public State of(StateDto target) {
        return (State) new State()
                .setName(target.getName())
                .setSegment(segmentService.findById(target.getSegment().getId()).orElseThrow(() -> new SegmentManagerException(SegmentMangerErrors.SEGMENT_NOT_FOUND)))
                .setValue(target.getValue())
                .setDefaultValue(target.getDefaultValue())
                .setIntegrationPointKey(target.getIntegrationPointKey())
                .setId(target.getId());
    }

    public StateDto of(State target) {
        return new StateDto()
                .setName(target.getName())
                .setSegment(SegmentShortInfoConverter.of(target.getSegment()))
                .setValue(target.getValue())
                .setDefaultValue(target.getDefaultValue())
                .setIntegrationPointKey(target.getIntegrationPointKey())
                .setId(target.getId());
    }
}
