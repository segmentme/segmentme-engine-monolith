package io.segmentme.core.api.config.security.dto;

import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class AuthUser extends User {

    //TODO need to map user
    public AuthUser(io.segmentme.core.db.domain.user.User user) {
        super(user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority("role")));
    }
}
