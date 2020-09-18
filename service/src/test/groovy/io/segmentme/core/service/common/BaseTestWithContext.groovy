package io.segmentme.core.service.common

import io.segmentme.core.db.SpringCoreDataApplication
import io.segmentme.core.service.configuration.test.ResourceHolder
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@Slf4j
@SpringBootTest(classes = SpringCoreDataApplication.class)
@ActiveProfiles("test")
@Import([ResourceHolder.class])
@AutoConfigureMockMvc
@ComponentScan("io.segmentme.core")
abstract class BaseTestWithContext extends Specification {

}
