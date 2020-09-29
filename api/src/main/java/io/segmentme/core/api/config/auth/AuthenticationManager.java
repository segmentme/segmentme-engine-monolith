package io.segmentme.core.api.config.auth;

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
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private final Auth0 auth0;

    private JwtAuthenticationProvider customJwtAuthenticationProvider;

    private final UserFacade userFacade;

    private static final String EMAIL_ATTRIBUTE = "https://segmentme.io:email";
    private static final String ACKNOWLEDGE_ATTRIBUTE = "https://segmentme.io:acknowledged";


    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, jwt -> {
            OAuth2Error error = new OAuth2Error("invalid_token", "The required audience is missing", null);

            if (jwt.getAudience().contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(error);
        });

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @PostConstruct
    void init() {
        customJwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder());
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
