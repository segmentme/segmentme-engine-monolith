package io.segmentme.core.db.service

import spock.lang.Specification

import static io.segmentme.core.db.service.AnalysisContextSchemaValidationService.*
import static io.segmentme.core.db.service.AnalysisContextSchemaValidationService.ContextValidationEntrySeverity.MID

class AnalysisContextSchemaValidationServiceImplTest extends Specification {
//    def "Validate"() {
//    }

    def "Of path #path and #code is #expectedSeverity"() {
        expect:
        AnalysisContextSchemaValidationServiceImpl.of(path, code).getSeverity() == expectedSeverity

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
}
