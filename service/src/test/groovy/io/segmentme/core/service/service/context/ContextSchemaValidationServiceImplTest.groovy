package io.segmentme.core.service.service.context


import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
import io.segmentme.core.service.context.ContextSchemaValidationServiceImpl
import io.segmentme.core.service.workspace.UserConfigurationServiceImpl
import spock.lang.Specification

import static io.segmentme.core.service.context.ContextSchemaValidationService.ContextValidationEntrySeverity.CRITICAL
import static io.segmentme.core.service.context.ContextSchemaValidationService.ContextValidationEntrySeverity.MID

class ContextSchemaValidationServiceImplTest extends Specification {

    static ResourceHolder resourceHolder = new ResourceHolder();
    def contextSchemaResolver = new ContextSchemaResolver(new UserConfigurationServiceImpl())

    def setupSpec() {
        resourceHolder.init();
    }

    def "Of path #path and #code is #expectedSeverity"() {
        expect:
        ContextSchemaValidationServiceImpl.of(path, code).getSeverity() == expectedSeverity

        where:
        path    | code                                                || expectedSeverity
        "root"  | CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT || ERRORS_SEVERITY.get(code)
        "root1" | ROOT_NODE_SHOULD_BE_OBJECT                          || ERRORS_SEVERITY.get(code)
        "root2" | ROOT_NODE_SHOULDNT_HAVE_SUBTUPES                    || ERRORS_SEVERITY.get(code)
        "root3" | NODE_TYPE_NOT_DEFINED                               || ERRORS_SEVERITY.get(code)
        "root4" | NODE_SUBTYPE_NOT_DEFINED                            || ERRORS_SEVERITY.get(code)
        "root5" | NODE_NAME_NOT_DEFINED                               || ERRORS_SEVERITY.get(code)
        "root6" | NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED                  || ERRORS_SEVERITY.get(code)
        "root7" | "UNKNOWN"                                           || MID
    }

    def "Test valid json should not contains issues"() {
        given:
        def validationService = new ContextSchemaValidationServiceImpl()

        def schema = contextSchemaResolver.resolve(resourceHolder.getValidJsonPayloadConfiguration())
        when:
        def validationResult = validationService.validate(schema);
        then:
        validationResult.isEmpty()
    }

    def "Test invalid json should  contains critical issues"() {
        given:
        def validationService = new ContextSchemaValidationServiceImpl()
        def schema = contextSchemaResolver.resolve(resourceHolder.getInvalidJsonPayloadConfiguration())
        when:
        def validationResult = validationService.validate(schema);
        then:
        !validationResult.findAll { it -> it.severity == CRITICAL }
                .findAll { it -> it.code == NODE_SUBTYPE_NOT_DEFINED }.isEmpty()
    }
}
