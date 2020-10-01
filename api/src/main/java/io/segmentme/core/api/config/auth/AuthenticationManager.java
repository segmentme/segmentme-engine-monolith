package io.segmentme.core.api.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.facade.UserFacade;
import io.segmentme.core.api.service.Auth0;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements org.springframework.security.authentication.AuthenticationManager {

    @Value("${auth0.audience}")
    private final String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private final String issuer;

    private final Auth0 auth0;

    private final UserFacade userFacade;

    private final ObjectMapper objectMapper;

    private JwtAuthenticationProvider customJwtAuthenticationProvider;

    @PostConstruct
    void init() {
        customJwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder());
        customJwtAuthenticationProvider.setJwtAuthenticationConverter(new JwtTokenConverter(objectMapper));
    }

    private JwtDecoder jwtDecoder() {
        var jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, jwt -> {
            OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);
            return jwt.getAudience().contains(audience) ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(error);
        });

        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication authenticate = customJwtAuthenticationProvider.authenticate(authentication);

        //check for app https://segmentme.io:persisted  true
        AuthUser authUser = (AuthUser) authenticate.getPrincipal();
        if (authenticate.isAuthenticated() && !Boolean.TRUE.equals(authUser.isAcknowledged())) {
            userFacade.acknowledgeUser(authUser.getId(), authUser.getEmail(), authUser.getFullName());
            auth0.acknowledge(authUser.getId());
        }

        return authenticate;
    }
}
