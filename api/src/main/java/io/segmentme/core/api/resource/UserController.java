package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.UserDetails;
import io.segmentme.core.api.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
    public void switchWorkspace(@RequestParam String workspaceId) {
        userFacade.switchWorkspace("userId", workspaceId);
    }
}
