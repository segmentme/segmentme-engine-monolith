package io.segmentme.core.service.analysis.state;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.service.state.StateService;
import io.segmentme.core.service.converter.StateConverter;
import io.segmentme.core.service.dto.analysis.state.StateDto;
import io.segmentme.core.service.exception.StateManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.StateManagerErrors.STATE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateManager {

    private final StateService stateService;

    public StateDto create(StateDto state) {
        State newState = StateConverter.of(state);
        return StateConverter.of(stateService.create(newState));
    }

    public List<StateDto> getByIntegrationPointKey(String integrationPoint) {
        return stateService.findByIntegrationPointKey(integrationPoint)
                .stream()
                .map(StateConverter::of)
                .collect(Collectors.toList());
    }

    public StateDto getById(String stateId) {
        return stateService.findById(stateId)
                .map(StateConverter::of)
                .orElseThrow(() -> new StateManagerException(String.format("State with id %s doesn't exist", stateId), STATE_NOT_FOUND));
    }

    public StateDto update(String stateId, StateDto state) {
        return stateService.findById(stateId)
                .map(it -> updateStateField(stateId, it, state))
                .map(stateService::update)
                .map(StateConverter::of)
                .orElseThrow(() -> new StateManagerException(String.format("State with id %s doesn't exist", stateId), STATE_NOT_FOUND));
    }

    public void delete(String stateId) {
        stateService.deleteById(stateId);
    }

    private State updateStateField(String stateId, State state, StateDto stateDto) {
        State updatedState = StateConverter.of(stateDto);
        return (State) state.setSegment(updatedState.getSegment())
                .setValue(updatedState.getValue())
                .setName(updatedState.getName())
                .setIntegrationPointKey(updatedState.getIntegrationPointKey())
                .setId(stateId);
    }
}
