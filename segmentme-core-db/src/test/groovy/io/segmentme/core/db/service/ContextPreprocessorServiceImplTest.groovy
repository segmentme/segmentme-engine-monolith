package io.segmentme.core.db.service

import io.segmentme.core.db.configuration.test.ResourceHolder
import spock.lang.Specification

class ContextPreprocessorServiceImplTest extends Specification {

    static ResourceHolder resourceHolder = new ResourceHolder();

    static schema = null

    def setupSpec() {
        resourceHolder.init();
        schema = new AnalysisContextSchemaResolver(new UserConfigurationServiceImpl()).resolve(resourceHolder.getValidJsonPayloadConfiguration())
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
