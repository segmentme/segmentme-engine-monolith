package io.segmentme.core.api.config;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@UtilityClass
public class SecurityUtils {

    public String currentUserId() {
        return Optional.ofNullable(currentUser())
                .map(AuthUser::getId)
                .orElseThrow(() -> new RuntimeException("Auth user id not found"));
    }

    public String currentEmail() {
        return Optional.ofNullable(currentUser())
                .map(AuthUser::getEmail)
                .orElseThrow(() -> new RuntimeException("Auth user email not found"));
    }


    public AuthUser currentUser() {
        return (AuthUser) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).map(Authentication::getPrincipal)
            .filter(it->AuthUser.class.isAssignableFrom(it.getClass())).orElse(null);
    }
}
