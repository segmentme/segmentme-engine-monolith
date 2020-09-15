package io.segmentme.core.db.service.context


import io.segmentme.core.db.configuration.test.ResourceHolder
import io.segmentme.core.db.domain.context.SchemaNodeType
import io.segmentme.core.db.service.UserConfigurationServiceImpl
import io.segmentme.core.db.service.context.ContextSchemaResolver
import spock.lang.Specification

import static io.segmentme.core.db.domain.context.ContextSchema.InlineType.of

class ContextSchemaResolverTest extends Specification {


    static ResourceHolder resourceHolder = new ResourceHolder();
    public static final ContextSchemaResolver resolver = new ContextSchemaResolver(new UserConfigurationServiceImpl())


    def setupSpec() {
        resourceHolder.init();
    }

    def "Test node counts should match 20"() {
        given:
        def schema = resolver.resolve(resourceHolder.getValidJsonPayloadConfiguration())
        expect:
        schema.getInlinePath().size() == 23
    }


    def "Valid JSON Check node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve((resourceHolder.getValidJsonPayloadConfiguration()))
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                              || type
        "user"                                || of(SchemaNodeType.OBJECT, null)
        "user.numbersArray"                   || of(SchemaNodeType.ARRAY, SchemaNodeType.NUMBER)
        "user.email"                          || of(SchemaNodeType.STRING, null)
        "user.name"                           || of(SchemaNodeType.STRING, null)
        "user.details"                        || of(SchemaNodeType.OBJECT, null)
        "user.details.gender"                 || of(SchemaNodeType.STRING, null)
        "user.details.address"                || of(SchemaNodeType.OBJECT, null)
        "user.details.address.addressLine1"   || of(SchemaNodeType.STRING, null)
        "user.details.address.state"          || of(SchemaNodeType.STRING, null)
        "user.details.birthDate"              || of(SchemaNodeType.DATE, null)
        "user.details.phone"                  || of(SchemaNodeType.STRING, null)
        "stringArray"                         || of(SchemaNodeType.ARRAY, SchemaNodeType.STRING)
        "objectArrays"                        || of(SchemaNodeType.ARRAY, SchemaNodeType.OBJECT)
        "objectArrays.id"                     || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber"        || of(SchemaNodeType.NUMBER, null)
        "objectArrays.subObjects"             || of(SchemaNodeType.ARRAY, SchemaNodeType.OBJECT)
        "objectArrays.subObjects.subObjectId" || of(SchemaNodeType.STRING, null)
        "objectArrays.numbersArray"           || of(SchemaNodeType.ARRAY, SchemaNodeType.NUMBER)
        "objectArrays.isActive"               || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"               || of(SchemaNodeType.DATE, null)
        "status"                              || of(SchemaNodeType.STRING, null)
        "fullAge"                             || of(SchemaNodeType.NUMBER, null)
        "weight"                              || of(SchemaNodeType.NUMBER, null)
    }

    def "Invalid JSON Check node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve((resourceHolder.getInvalidJsonPayloadConfiguration()))
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                       || type
        "objectArrays"                 || of(SchemaNodeType.ARRAY, SchemaNodeType.UNDEFINED)
        "objectArrays.id"              || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber" || of(SchemaNodeType.NUMBER, null)
        "objectArrays.isActive"        || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"        || of(SchemaNodeType.DATE, null)
    }
}
