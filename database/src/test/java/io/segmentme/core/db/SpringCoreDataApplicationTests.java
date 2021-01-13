package io.segmentme.core.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

@SpringBootTest
class SpringCoreDataApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    void contextLoads() {
        Assert.notNull(mongoTemplate, "Started");
    }

}
