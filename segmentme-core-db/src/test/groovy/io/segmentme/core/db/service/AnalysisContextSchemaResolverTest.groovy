package io.segmentme.core.db.service


import io.segmentme.core.db.configuration.test.ResourceHolder
import io.segmentme.core.db.domain.context.SchemaNodeType
import spock.lang.Specification

import static io.segmentme.core.db.domain.context.AnalysisContextSchema.InlineType.of

class AnalysisContextSchemaResolverTest extends Specification {


    static ResourceHolder resourceHolder = new ResourceHolder();
    public static final AnalysisContextSchemaResolver resolver = new AnalysisContextSchemaResolver(new UserConfigurationServiceImpl())


    def setupSpec() {
        resourceHolder.init();
    }

    def "Test node counts should match 20"() {
        given:
        def schema = resolver.resolve(resourceHolder.getValidJsonPayloadConfiguration())
        expect:
        schema.getInlinePath().size() == 20
    }


    def "Valid JSON Check node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve((resourceHolder.getValidJsonPayloadConfiguration()))
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                            || type
        "user"                              || of(SchemaNodeType.OBJECT, null)
        "user.numbersArray"                 || of(SchemaNodeType.ARRAY, SchemaNodeType.NUMBER)
        "user.email"                        || of(SchemaNodeType.STRING, null)
        "user.name"                         || of(SchemaNodeType.STRING, null)
        "user.details"                      || of(SchemaNodeType.OBJECT, null)
        "user.details.gender"               || of(SchemaNodeType.STRING, null)
        "user.details.address"              || of(SchemaNodeType.OBJECT, null)
        "user.details.address.addressLine1" || of(SchemaNodeType.STRING, null)
        "user.details.address.state"        || of(SchemaNodeType.STRING, null)
        "user.details.birthDate"            || of(SchemaNodeType.DATE, null)
        "user.details.phone"                || of(SchemaNodeType.STRING, null)
        "stringArray"                       || of(SchemaNodeType.ARRAY, SchemaNodeType.STRING)
        "objectArrays"                      || of(SchemaNodeType.ARRAY, SchemaNodeType.OBJECT)
        "objectArrays.id"                   || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber"      || of(SchemaNodeType.NUMBER, null)
        "objectArrays.isActive"             || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"             || of(SchemaNodeType.DATE, null)
        "status"                            || of(SchemaNodeType.STRING, null)
        "fullAge"                           || of(SchemaNodeType.NUMBER, null)
        "weight"                            || of(SchemaNodeType.NUMBER, null)
    }

    def "Invalid JSON Check node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve((resourceHolder.getInvalidJsonPayloadConfiguration()))
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                            || type
        "objectArrays"                      || of(SchemaNodeType.ARRAY, SchemaNodeType.UNDEFINED)
        "objectArrays.id"                   || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber"      || of(SchemaNodeType.NUMBER, null)
        "objectArrays.isActive"             || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"             || of(SchemaNodeType.DATE, null)
    }
}
