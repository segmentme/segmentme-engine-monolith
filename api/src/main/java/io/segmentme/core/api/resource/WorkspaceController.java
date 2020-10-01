package io.segmentme.core.api.resource;

import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
public class WorkspaceController {
    private final WorkspaceManager workspaceManager;

    @PostMapping("/{workspaceId}/integration-point")
    public IntegrationPoint createIntegrationPoint(@PathVariable String workspaceId, @RequestParam String name) {
        return workspaceManager.addIntegrationPoint(workspaceId, name);
    }

    @PutMapping("/{workspaceId}/configuration")
    public void updateWorkspaceConfguration(@PathVariable String workspaceId, @RequestBody WorkspaceConfiguration workspaceConfiguration) {
        workspaceManager.updateConfiguration(workspaceId, workspaceConfiguration);
    }

    @PutMapping("/{workspaceId}/integration-point")
    public void updateIntegrationPoint(@PathVariable String workspaceId, @RequestBody IntegrationPoint integrationPoint) {
        workspaceManager.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    @DeleteMapping("/{workspaceId}/integration-point/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIntegrationPoint(@PathVariable String workspaceId, @PathVariable String key) {
        workspaceManager.removeIntegrationPoint(workspaceId, key);
    }
}
