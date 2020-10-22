package io.segmentme.core.api.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.api.config.AuthUser
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import lombok.SneakyThrows
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

@AutoConfigureMockMvc
class BaseControllerTest extends BaseTestWithContext {

    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected ObjectMapper objectMapper

    @SpringBean
    protected AuthenticationManager authenticationManager = Mock()

    @Autowired
    protected ResourceHolder resourceHolder

    @SneakyThrows
    protected String serializeToJson(Object o) {
        return objectMapper.writeValueAsString(o)
    }

    protected MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder builder, String id = uuid(), String email = 'test@test.test') {
        authenticationManager.authenticate(_) >> accessToken(id, email)
        return builder.header("Authorization", "Bearer ${uuid()}")
    }

    private AbstractAuthenticationToken accessToken(String id = uuid(), String email = 'test@test.test') {
        return new TestingAuthenticationToken(new AuthUser().setEmail(email).setId(id).setAcknowledged(true), null)
    }

    private String uuid() {
        return UUID.randomUUID().toString()
    }

    protected SegmentDto getSegment(String segmentName) {
        return resourceHolder.getSegmentsDto()
                .stream()
                .filter(it -> it.getName().equalsIgnoreCase(segmentName))
                .findFirst()
                .get()
    }

    protected ResultActions sendRequest(MockHttpServletRequestBuilder request, Object body = null){
        def preparedRequest = auth(request).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        return body != null ? mockMvc.perform(preparedRequest.content(serializeToJson(body))) : mockMvc.perform(preparedRequest)
    }
}
