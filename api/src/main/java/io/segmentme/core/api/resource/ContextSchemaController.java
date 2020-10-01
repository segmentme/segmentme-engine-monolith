package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.ContextSchemaDetails;
import io.segmentme.core.api.facade.ContextSchemaFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
