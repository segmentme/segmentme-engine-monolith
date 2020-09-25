package io.segmentme.core.api.resource;

import io.segmentme.core.api.dto.UserDetails;
import io.segmentme.core.api.facade.UserFacade;
import io.segmentme.core.service.dto.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping
    public UserDetails createUser(@RequestBody UserHolder user) {
        return userFacade.registerUser(user);
    }

    @PostMapping
    public UserDetails getCurrentUserDetails() {
        return userFacade.getUserDetails("userId");
    }

    @PutMapping
    public void switchWorkspace(@RequestParam String workspaceId) {
        userFacade.switchWorkspace("userId", workspaceId);
    }
}
