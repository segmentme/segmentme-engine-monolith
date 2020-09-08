package io.segmentme.core.db.common

import groovy.util.logging.Slf4j
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import spock.lang.Specification

@Slf4j
@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
abstract class BaseDatabaseTest extends Specification {

    @Autowired
    protected MongoTemplate mongoTemplate

}
