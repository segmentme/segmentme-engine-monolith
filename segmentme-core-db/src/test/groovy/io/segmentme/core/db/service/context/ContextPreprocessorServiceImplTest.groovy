package io.segmentme.core.db.service.context

import io.segmentme.core.db.configuration.test.ResourceHolder
import io.segmentme.core.db.service.UserConfigurationServiceImpl
import io.segmentme.core.db.service.context.ContextPreprocessorServiceImpl
import io.segmentme.core.db.service.context.ContextSchemaResolver
import spock.lang.Specification

class ContextPreprocessorServiceImplTest extends Specification {

    static ResourceHolder resourceHolder = new ResourceHolder();

    static schema = null

    def setupSpec() {
        resourceHolder.init();
        schema = new ContextSchemaResolver(new UserConfigurationServiceImpl()).resolve(resourceHolder.getValidJsonPayloadConfiguration())
    }

    def "PrepareContext"() {
        given:
        def json = resourceHolder.getValidJsonPayloadConfiguration()
        when:
        def result = new ContextPreprocessorServiceImpl(new UserConfigurationServiceImpl()).prepareContext(json, schema)
        then:
        result != null;
    }
}
