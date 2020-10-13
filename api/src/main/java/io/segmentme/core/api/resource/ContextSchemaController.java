package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.*;
import io.segmentme.core.api.facade.ContextSchemaFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/context-schema")
@RequiredArgsConstructor
public class ContextSchemaController {

    private final ContextSchemaFacade contextSchemaFacade;

    @GetMapping
    public List<ContextSchemaDetails> getContextSchema(@AuthenticationPrincipal AuthUser authUser,
                                                       @RequestParam String workspaceId,
                                                       @RequestParam(defaultValue = "true") boolean shortForm) {
        return contextSchemaFacade.getByWorkspace(authUser.getId(), workspaceId, shortForm);
    }

    @DeleteMapping("/{id}")
    public void deleteContextSchema(@AuthenticationPrincipal AuthUser authUser, @PathVariable String id) {
        contextSchemaFacade.delete(authUser, id);
    }

    @GetMapping("/{id}")
    public ContextSchemaDetails getContexSchemaDetails(@AuthenticationPrincipal AuthUser authUser, @PathVariable String id) {
        return contextSchemaFacade.getById(authUser, id);
    }

    @PostMapping("/resolve")
    public ContextSchemaValidationResult resolveContextSchema(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId, @RequestBody JsonNode payload) {
        return contextSchemaFacade.resolve(authUser.getId(), workspaceId, payload);
    }

    @PostMapping("/validate")
    public ContextSchemaValidationResult validateContextSchema(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId,
                                                               @RequestBody ContextSchemaValidationRequest validationRequest) {
        return contextSchemaFacade.validate(authUser.getId(), workspaceId, validationRequest);
    }

    @PostMapping
    public ContextSchemaDetails create(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId, @RequestBody ContextSchemaCreateRequest contextSchemaCreateRequest) {
        return contextSchemaFacade.create(authUser.getId(), workspaceId, contextSchemaCreateRequest);
    }

    @PutMapping("/{id}")
    public ContextSchemaDetails update(@AuthenticationPrincipal AuthUser authUser, @PathVariable String id, @RequestBody ContextSchemaUpdateRequest payload) {
        return contextSchemaFacade.update(authUser.getId(), id,payload);
    }
}
