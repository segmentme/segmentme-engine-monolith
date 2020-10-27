package io.segmentme.core.api.security;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.service.state.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateSecurityService {

    private final StateService stateService;

    private final SecurityService securityService;

    public boolean isValidState(String stateId, String userId) {
        return stateService.findById(stateId)
                .map(State::getIntegrationPointKey)
                .map(it -> securityService.isValidIntegrationPointKey(it, userId))
                .orElse(false);
    }
}
