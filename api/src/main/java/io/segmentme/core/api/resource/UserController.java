package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.UserDetails;
import io.segmentme.core.api.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping
    public UserDetails getCurrentUserDetails(@AuthenticationPrincipal AuthUser authUser) {
        return userFacade.getUserDetails(authUser.getId());
    }

    @PutMapping
    @PreAuthorize("@workspaceSecurityService.isWorkspaceMember(#workspaceId)")
    public void switchWorkspace(@RequestParam String workspaceId,
                                @AuthenticationPrincipal AuthUser authUser) {
        userFacade.switchWorkspace(authUser.getId(), workspaceId);
    }
}
