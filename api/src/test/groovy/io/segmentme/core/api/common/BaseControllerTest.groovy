package io.segmentme.core.api.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.service.common.BaseTestWithContext
import lombok.SneakyThrows
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

import java.time.Duration
import java.time.Instant

@AutoConfigureMockMvc
class BaseControllerTest extends BaseTestWithContext {

    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected ObjectMapper objectMapper

    @SpringBean
    protected AuthenticationManager authenticationManager = Mock()


    @SneakyThrows
    protected String serializeToJson(Object o) {
        return objectMapper.writeValueAsString(o)
    }

    protected MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder builder) {
        def token = accessToken()
        def authorities = new HashSet<>(AuthorityUtils.createAuthorityList("USER"))
        authenticationManager.authenticate(_) >> new BearerTokenAuthentication(new DefaultOAuth2AuthenticatedPrincipal(Map.of("sub", "test"), authorities), token, authorities)
        return builder.header("Authorization", "Bearer " + token.getTokenValue())
    }

    private OAuth2AccessToken accessToken() {
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, UUID.randomUUID().toString(), Instant.now(), Instant.now() + Duration.ofDays(1));
    }

}
