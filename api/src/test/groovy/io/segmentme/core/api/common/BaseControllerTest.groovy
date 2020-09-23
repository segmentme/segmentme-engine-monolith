package io.segmentme.core.api.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.service.common.BaseTestWithContext
import lombok.SneakyThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
class BaseControllerTest extends BaseTestWithContext {

    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected ObjectMapper objectMapper

    @SneakyThrows
    protected String serializeToJson(Object o) {
        return objectMapper.writeValueAsString(o);
    }

}
