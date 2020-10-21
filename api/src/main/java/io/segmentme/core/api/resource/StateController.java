package io.segmentme.core.api.resource;

import io.segmentme.core.service.analysis.state.StateManager;
import io.segmentme.core.service.dto.analysis.state.StateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    private final StateManager stateManager;

    @PostMapping("/workspace/{workspaceId}")
    public StateDto create(@PathVariable String workspaceId, @Valid @RequestBody StateDto state) {
        return stateManager.create(workspaceId, state);
    }

    @GetMapping("/workspace/{workspaceId}")
    public List<StateDto> getAllInWorkspace(@PathVariable String workspaceId) {
        return stateManager.getByWorkspaceId(workspaceId);
    }

    @GetMapping("/{stateId}")
    public StateDto getById(@PathVariable String stateId) {
        return stateManager.getById(stateId);
    }

    @PutMapping("/{stateId}")
    public StateDto update(@PathVariable String stateId, @Valid @RequestBody StateDto state) {
        return stateManager.update(stateId, state);
    }

    @DeleteMapping("/{stateId}")
    public void delete(@PathVariable String stateId) {
        stateManager.delete(stateId);
    }
}
