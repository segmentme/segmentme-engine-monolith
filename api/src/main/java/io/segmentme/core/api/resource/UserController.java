package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.UserDetails;
import io.segmentme.core.api.facade.UserFacade;
import io.segmentme.core.api.service.Auth0Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    private final Auth0Service auth0Service;

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

    @GetMapping("/resend-verification-email")
    public void resendVerificationEmail(@AuthenticationPrincipal AuthUser authUser){
        auth0Service.resendVerificationEmail(authUser.getId());
    }
}
