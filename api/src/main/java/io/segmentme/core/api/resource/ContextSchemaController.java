package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.context.*;
import io.segmentme.core.api.facade.ContextSchemaFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/context-schema")
@RequiredArgsConstructor
@PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
public class ContextSchemaController {

    private final ContextSchemaFacade contextSchemaFacade;

    @GetMapping
    public List<ContextSchemaBasicInfo> getContextSchema(@RequestParam String workspaceId,
                                                         @RequestParam(defaultValue = "true") boolean shortForm) {
        return contextSchemaFacade.getByWorkspace(workspaceId, shortForm);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#id)")
    public void deleteContextSchema(@PathVariable String id) {
        contextSchemaFacade.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#id)")
    public ContextSchemaFullDetails getContexSchemaDetails(@PathVariable String id) {
        return contextSchemaFacade.getById(id);
    }

    @PostMapping("/resolve")
    public ContextSchemaResolveResult resolveContextSchema(@RequestParam String workspaceId, @RequestBody JsonNode payload) {
        return contextSchemaFacade.resolve(workspaceId, payload);
    }

    @PostMapping("/validate")
    public ContextSchemaValidationResult validateContextSchema(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId,
                                                               @RequestBody ContextSchemaValidationRequest validationRequest) {
        return contextSchemaFacade.validate(workspaceId, validationRequest);
    }

    @PostMapping
    @PreAuthorize("@securityService.isValidIntegrationPointKey(#contextSchemaCreateRequest.integrationPointKey, #authUser.id)")
    public ContextSchemaBasicInfo create(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId, @RequestBody ContextSchemaCreateRequest contextSchemaCreateRequest) {
        return contextSchemaFacade.create(contextSchemaCreateRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#id)")
    public ContextSchemaBasicInfo update(@PathVariable String id, @RequestBody ContextSchemaUpdateRequest payload) {
        return contextSchemaFacade.update(id, payload);
    }
}
