package io.segmentme.core.api.config.auth;

import io.segmentme.core.api.facade.UserFacade;
import io.segmentme.core.api.service.Auth0;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements org.springframework.security.authentication.AuthenticationManager {

    private static final String EMAIL_ATTRIBUTE = "https://segmentme.io:email";

    private static final String ACKNOWLEDGE_ATTRIBUTE = "https://segmentme.io:acknowledged";

    @Value("${auth0.audience}")
    private final String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private final String issuer;

    private final Auth0 auth0;

    private final UserFacade userFacade;

    private JwtAuthenticationProvider customJwtAuthenticationProvider;

    @PostConstruct
    void init() {
        customJwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder());
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
        Jwt credentials = (Jwt) authenticate.getCredentials();
        if (authenticate.isAuthenticated() && !Boolean.parseBoolean(credentials.getClaims().getOrDefault(ACKNOWLEDGE_ATTRIBUTE, false).toString())) {
            String id = credentials.getClaims().get("sub").toString();
            userFacade.acknowledgeUser(id, credentials.getClaims().get(EMAIL_ATTRIBUTE).toString());
            auth0.acknowledge(id);
        }

        return authenticate;
    }
}
