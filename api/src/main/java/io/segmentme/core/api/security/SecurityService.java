package io.segmentme.core.api.security;

import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final WorkspaceService workspaceService;

    public boolean isValidIntegrationPointKey(String integrationPointKey, String userId) {
        return workspaceService.findByIntegrationPointKey(integrationPointKey)
                .map(Workspace::getUserProfiles)
                .stream()
                .flatMap(Collection::stream)
                .map(UserProfile::getUserId)
                .filter(it -> it.equalsIgnoreCase(userId))
                .findFirst()
                .map(it -> Boolean.TRUE)
                .orElse(false);
    }
}
