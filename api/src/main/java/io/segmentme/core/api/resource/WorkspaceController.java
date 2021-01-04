package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.WorkspaceDatesValidationRequest;
import io.segmentme.core.api.dto.WorkspaceDatesValidationResponse;
import io.segmentme.core.api.dto.WorkspaceDetails;
import io.segmentme.core.api.dto.WorkspaceUserProfile;
import io.segmentme.core.api.facade.WorkspaceFacade;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
@PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
public class WorkspaceController {

    private final WorkspaceFacade workspaceFacade;

    @GetMapping("/{workspaceId}")
    public WorkspaceDetails getWorkspace(@PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceDetails(workspaceId);
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public WorkspaceDetails createWorkspace(@AuthenticationPrincipal AuthUser currentUser,
                                            @RequestParam String name) {
        return workspaceFacade.createWorkspace(currentUser.getId(), name);
    }

    @PostMapping("/{workspaceId}/integration-point")
    public IntegrationPoint createIntegrationPoint(@PathVariable String workspaceId, @RequestParam String name) {
        return workspaceFacade.addIntegrationPoint(workspaceId, name);
    }

    @PutMapping("/{workspaceId}/configuration")
    public void updateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceConfiguration workspaceConfiguration) {
        workspaceFacade.updateConfiguration(workspaceId, workspaceConfiguration);
    }

    @PutMapping("/{workspaceId}")
    public void updateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceDetails workspaceDetails) {
        workspaceFacade.updateWorkspace(workspaceId, workspaceDetails);
    }

    @PostMapping("/{workspaceId}/configuration/date-format/validate")
    public WorkspaceDatesValidationResponse validateWorkspaceConfiguration(@PathVariable String workspaceId, @RequestBody WorkspaceDatesValidationRequest validationRequest) {
        return workspaceFacade.validateDateFormats(validationRequest);
    }

    @PutMapping("/{workspaceId}/integration-point")
    public void updateIntegrationPoint(@PathVariable String workspaceId, @RequestBody IntegrationPoint integrationPoint) {
        workspaceFacade.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    @DeleteMapping("/{workspaceId}/integration-point/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntegrationPoint(@PathVariable String workspaceId, @PathVariable String key) {
        workspaceFacade.removeIntegrationPoint(workspaceId, key);
    }

    @DeleteMapping("/{workspaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(@PathVariable String workspaceId) {
        workspaceFacade.removeWorkspace(workspaceId);
    }


    @GetMapping("/{workspaceId}/profiles")
    public List<WorkspaceUserProfile> getWorkspaceList(@PathVariable String workspaceId) {
        return workspaceFacade.getWorkspaceProfiles(workspaceId);
    }

}
