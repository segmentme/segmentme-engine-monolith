package io.segmentme.core.api.config.security.oauth;

import io.segmentme.core.api.config.security.property.SecuritySettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfiguration {

    private final SecuritySettings securitySettings;

    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer() {
        var converter = new JwtAccessTokenConverter();
        TokenConverter tokenConverter = new TokenConverter();
        converter.setAccessTokenConverter(tokenConverter);
        converter.setVerifierKey(securitySettings.getJwt().getVerifierKey());
        converter.setKeyPair(getJwtKeyPair());
        return converter;
    }

    private KeyPair getJwtKeyPair() {
        return Optional.of(new KeyStoreKeyFactory(securitySettings.getKeyStore(), securitySettings.getPassword().toCharArray()))
                .map(it -> it.getKeyPair(securitySettings.getJwtAlias()))
                .orElseThrow(() -> new RuntimeException("Unable to load KeyPair for JWT signing!"));
    }
}
