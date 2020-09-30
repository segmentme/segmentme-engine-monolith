package io.segmentme.core.api.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.api.config.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

@RequiredArgsConstructor
public class JwtTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ObjectMapper objectMapper;

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
            = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtToken(jwt, convertToPrincipal(jwt), extractAuthorities(jwt));
    }

    private AuthUser convertToPrincipal(Jwt jwt) {
        return objectMapper.convertValue(jwt.getClaims(), AuthUser.class);
    }

    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return this.jwtGrantedAuthoritiesConverter.convert(jwt);
    }
}
