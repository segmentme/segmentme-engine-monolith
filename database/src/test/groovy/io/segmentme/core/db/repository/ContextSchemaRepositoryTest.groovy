package io.segmentme.core.db.repository

import io.segmentme.core.db.domain.context.ContextSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class ContextSchemaRepositoryTest extends Specification {

    @Autowired
    private ContextSchemaRepository repository

    def 'find context schema short form'() {

        given:
        def schemas = [generateSchema(), generateSchema(), generateSchema()]
        schemas = repository.saveAll(schemas)
        when:
        def find = repository.findShortFormByIntegrationPointKeyIn([schemas[0].getIntegrationPointKey(), schemas[1].getIntegrationPointKey()])

        then:
        find.size() == 2
        find.forEach(it -> {
            assert it.getIntegrationPointKey() != null
            assert it.getName() != null
            assert it.getRawPayload() == null
        })
    }

    private ContextSchema generateSchema() {
        new ContextSchema().setName("name").setRawPayload("some raw payload").setIntegrationPointKey(UUID.randomUUID().toString())
    }


}
