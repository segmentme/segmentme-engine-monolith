package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.service.dto.analysis.state.StateDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StateConverter {

    public State of(StateDto target) {
        return (State) new State()
                .setName(target.getName())
                .setSegment(SegmentConverter.of(target.getSegment(), null, null))
                .setValue(target.getValue())
                .setContextId(target.getContextId())
                .setIntegrationPoint(target.getIntegrationPoint())
                .setId(target.getId());
    }

    public StateDto of(State target) {
        return new StateDto()
                .setName(target.getName())
                .setSegment(SegmentConverter.of(target.getSegment()))
                .setValue(target.getValue())
                .setContextId(target.getContextId())
                .setIntegrationPoint(target.getIntegrationPoint())
                .setId(target.getId());
    }
}
