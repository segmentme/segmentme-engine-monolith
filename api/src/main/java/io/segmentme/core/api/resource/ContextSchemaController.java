package io.segmentme.core.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.ContextSchemaDetails;
import io.segmentme.core.api.facade.ContextSchemaFacade;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;
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
    public List<ContextSchemaDetails> getContextSchema(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId) {
        return contextSchemaFacade.getByWorkspace(authUser.getId(), workspaceId);
    }


    @PostMapping("/resolve")
    public ContextSchemaHolder getContextSchema(@AuthenticationPrincipal AuthUser authUser, @RequestParam String workspaceId, @RequestBody JsonNode payload) {
        return contextSchemaFacade.resolve(authUser.getId(),workspaceId, payload);
    }
}
