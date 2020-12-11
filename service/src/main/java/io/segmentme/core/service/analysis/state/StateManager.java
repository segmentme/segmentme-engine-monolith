package io.segmentme.core.service.analysis.state;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.service.state.StateService;
import io.segmentme.core.service.converter.StateConverter;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.dto.analysis.state.StateDto;
import io.segmentme.core.service.exception.StateManagerException;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.StateManagerErrors.DUPLICATED_STATE_NAME;
import static io.segmentme.core.service.exception.error.StateManagerErrors.STATE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateManager {

    private final StateService stateService;

    private final WorkspaceManager workspaceManager;

    private final StateConverter stateConverter;

    public StateDto create(StateDto state) {
        if (stateService.findByNameAndIntegrationPointKey(state.getName(), state.getIntegrationPointKey()).isPresent()) {
            throw new StateManagerException(String.format("State with name %s and integrationPointKey %s already exists", state.getName(), state.getIntegrationPointKey()), DUPLICATED_STATE_NAME);
        }

        State newState = stateConverter.of(state);
        return stateConverter.of(stateService.create(newState));
    }

    public List<StateDto> getByWorkspaceId(String workspaceId) {
        List<String> integrationPointKeys = Optional.ofNullable(workspaceManager.getWorkspace(workspaceId))
                .map(WorkspaceHolder::getIntegrationPoints)
                .stream()
                .flatMap(Collection::stream)
                .map(IntegrationPoint::getKey)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(integrationPointKeys)) {
            return List.of();
        }

        return stateService.findByIntegrationPointKeys(integrationPointKeys)
                .stream()
                .map(stateConverter::of)
                .collect(Collectors.toList());
    }

    public StateDto getById(String stateId) {
        return stateService.findById(stateId)
                .map(stateConverter::of)
                .orElseThrow(() -> new StateManagerException(String.format("State with id %s doesn't exist", stateId), STATE_NOT_FOUND));
    }

    public StateDto update(String stateId, StateDto state) {
        return stateService.findById(stateId)
                .map(it -> updateStateField(stateId, it, state))
                .map(stateService::update)
                .map(stateConverter::of)
                .orElseThrow(() -> new StateManagerException(String.format("State with id %s doesn't exist", stateId), STATE_NOT_FOUND));
    }

    public void delete(String stateId) {
        stateService.deleteById(stateId);
    }

    private State updateStateField(String stateId, State state, StateDto stateDto) {
        State updatedState = stateConverter.of(stateDto);
        return (State) state.setSegment(updatedState.getSegment())
                .setValue(updatedState.getValue())
                .setDefaultValue(updatedState.getDefaultValue())
                .setName(updatedState.getName())
                .setIntegrationPointKey(updatedState.getIntegrationPointKey())
                .setSegment(updatedState.getSegment())
                .setId(stateId);
    }
}
