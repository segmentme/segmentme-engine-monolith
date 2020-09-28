package io.segmentme.core.api.config.security.oauth;

import io.segmentme.core.api.config.security.property.SecuritySettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Slf4j
@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    private static final String[] GRANT_TYPES = {"refresh_token", "password"};

    private final PasswordEncoder passwordEncoder;
    private final SecuritySettings securitySettings;
    private final UserDetailsService userDetailsService;
    private final JwtAccessTokenConverter jwtTokenEnhancer;
    private final AuthenticationManager authenticationManager;

    @Bean
    public TokenStore tokeStore() {
        return new JwtTokenStore(jwtTokenEnhancer);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        var jwtValidity = securitySettings.getJwt().getValidity();

        clients.inMemory()
                .withClient("client")
                .secret(passwordEncoder.encode(StringUtils.EMPTY))
                .autoApprove(true)
                .scopes("app")
                .authorizedGrantTypes(GRANT_TYPES)
                .accessTokenValiditySeconds(jwtValidity.getAccess())
                .refreshTokenValiditySeconds(jwtValidity.getRefresh());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokeStore())
                .tokenEnhancer(jwtTokenEnhancer)
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .reuseRefreshTokens(false);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }
}
