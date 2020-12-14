package io.segmentme.core.api.security;

import io.segmentme.core.api.config.SecurityUtils;
import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.service.state.StateService;
import io.segmentme.core.db.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateSecurityService {

    private final StateService stateService;

    private final SecurityService securityService;

    private final UserService userService;

    public boolean isManagedState(String stateId, String userId) {
        String id = userService.findByExternalId(SecurityUtils.currentUserId()).get().getId();
        return stateService.findById(stateId)
                .map(State::getIntegrationPointKey)
                .map(it -> securityService.isValidIntegrationPointKey(it, id))
                .orElse(false);
    }
}
