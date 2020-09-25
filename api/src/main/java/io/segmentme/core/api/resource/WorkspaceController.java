package io.segmentme.core.api.resource;

import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
public class WorkspaceController {
    private final WorkspaceManager workspaceManager;

    @PostMapping("/{id}/integration-point")
    public IntegrationPoint createIntegrationPoint(@PathVariable String id) {
        return workspaceManager.addIntegrationPoint(id);
    }

    @PutMapping("/{id}/configuration")
    public void updateWorkspaceConfguration(@PathVariable String id, @RequestBody WorkspaceConfiguration workspaceConfiguration) {
        workspaceManager.updateConfiguration(id, workspaceConfiguration);
    }

    @DeleteMapping("/{id}/integration-point/{key}")
    public IntegrationPoint createIntegrationPoint(@PathVariable String id, @PathVariable String key) {
        return workspaceManager.addIntegrationPoint(id);
    }
}
