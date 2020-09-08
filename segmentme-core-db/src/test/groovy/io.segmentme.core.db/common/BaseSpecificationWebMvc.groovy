package io.segmentme.core.db.common


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@WebMvcTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class BaseSpecificationWebMvc extends Specification {

    @Autowired
    protected MockMvc mockMvc

}
