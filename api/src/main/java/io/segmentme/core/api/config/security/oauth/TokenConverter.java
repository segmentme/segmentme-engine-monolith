package io.segmentme.core.api.config.security.oauth;

import io.segmentme.core.api.config.security.dto.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
class TokenConverter extends DefaultAccessTokenConverter {

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        Map<String, Object> tokenValue = new HashMap<>(super.convertAccessToken(token, authentication));
        if (authentication.getUserAuthentication().getPrincipal() instanceof AuthUser) {
            //TODO add custom token attribute
            tokenValue.put("TEST_PROPERTY", "TEST_VALUE");
        }
        return tokenValue;
    }
}
