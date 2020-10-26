package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.WorkspaceDatesValidationRequest;
import io.segmentme.core.api.dto.WorkspaceDatesValidationResponse;
import io.segmentme.core.api.dto.WorkspaceDetails;
import io.segmentme.core.api.dto.WorkspaceUserProfile;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
public class WorkspaceController {
    private final WorkspaceManager workspaceManager;
    private final WorkspaceFacade workspaceFacade;

    @GetMapping("/{workspaceId}")
    @PreAuthorize("@workspaceSecurityService.isMyWorkspace(#workspaceId, #currentUser.id)")
    public WorkspaceDetails getWorkspace(@AuthenticationPrincipal AuthUser currentUser,
                                         @PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceDetails(workspaceId);
    }

    @PostMapping
    public WorkspaceDetails createWorkspace(@AuthenticationPrincipal AuthUser currentUser,
                                            @RequestParam String name) {
        return workspaceFacade.createWorkspace(currentUser.getId(), name);
    }

    @PostMapping("/{workspaceId}/integration-point")
    @PreAuthorize("@workspaceSecurityService.isMyWorkspace(#workspaceId, #currentUser.id)")
    public IntegrationPoint createIntegrationPoint(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @RequestParam String name) {
        return workspaceFacade.addIntegrationPoint(currentUser, workspaceId, name);
    }

    @PutMapping("/{workspaceId}/configuration")
    public void updateWorkspaceConfiguration(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @RequestBody WorkspaceConfiguration workspaceConfiguration) {
        workspaceFacade.updateConfiguration(currentUser.getId(), workspaceId, workspaceConfiguration);
    }

    @PutMapping("/{workspaceId}")
    public void updateWorkspaceConfiguration(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @RequestBody WorkspaceDetails workspaceDetails) {
        workspaceFacade.updateWorkspace(currentUser.getId(), workspaceId, workspaceDetails);
    }

    @PostMapping("/{workspaceId}/configuration/date-format/validate")
    public WorkspaceDatesValidationResponse validateWorkspaceConfiguration(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @RequestBody WorkspaceDatesValidationRequest validationRequest) {
        return workspaceFacade.validateDateFormats(currentUser.getId(), workspaceId, validationRequest);
    }

    @PutMapping("/{workspaceId}/integration-point")
    public void updateIntegrationPoint(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @RequestBody IntegrationPoint integrationPoint) {
        workspaceFacade.updateIntegrationPoint(currentUser.getId(), workspaceId, integrationPoint);
    }

    @DeleteMapping("/{workspaceId}/integration-point/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntegrationPoint(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId, @PathVariable String key) {
        workspaceFacade.removeIntegrationPoint(currentUser.getId(), workspaceId, key);
    }

    @DeleteMapping("/{workspaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId) {
        workspaceFacade.removeWorkspace(currentUser.getId(), workspaceId);
    }


    @GetMapping("/{workspaceId}/profiles")
    public List<WorkspaceUserProfile> getWorkspaceList(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceProfiles(workspaceId);
    }

}
