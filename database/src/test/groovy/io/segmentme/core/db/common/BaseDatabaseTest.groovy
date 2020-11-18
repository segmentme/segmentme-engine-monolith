package io.segmentme.core.db.common


//import de.flapdoodle.embed.mongo.MongodExecutable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
abstract class BaseDatabaseTest extends Specification {

    @Autowired
    protected MongoTemplate mongoTemplate

//    @Autowired
//    protected MongodExecutable mongodExecutable

}
