package io.segmentme.core.db.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.db.domain.context.SchemaNodeType
import spock.lang.Specification

import static io.segmentme.core.db.domain.context.AnalysisContextSchema.InlineType.of

class AnalysisContextSchemaResolverTest extends Specification {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode objectToResolve = null;

    def setup() {
        objectToResolve = objectMapper.readValue("{\n" +
                "    \"user\": {\n" +
                "        \"numbersArray\":[12,23,22.4],\n" +
                "        \"email\": \"vladislavkondratenko@coherentsolutions.com\",\n" +
                "        \"name\": \"Vladislav\",\n" +
                "        \"details\": {\n" +
                "            \"gender\": \"\",\n" +
                "            \"address\": {\n" +
                "                \"addressLine1\": \"Dasdsadas\",\n" +
                "                \"state\": \"NU\"\n" +
                "            },\n" +
                "            \"birthDate\": \"2006-10-22\",\n" +
                "            \"phone\": \"213123\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"stringArray\":[\"11\",\"44\"],\n" +
                "    \"objectArrays\": [\n" +
                "        {\n" +
                "            \"id\": \"02af8297654248b5a3d17b0f173fa581\",\n" +
                "            \"agreementNumber\": 123,\n" +
                "            \"isActive\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"02af8297654248b5a3d17b0f173fa581\",\n" +
                "          \"isActive\": true,\n" +
                "          \"dateTime\": \"2010-01-01T12:00:00Z\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"ACTIVE\",\n" +
                "    \"fullAge\": 12,\n" +
                "    \"weight\":199999999.123232\n" +
                "}", JsonNode.class)
    }

    def "Test node counts should match 20"() {
        given:
        def schema = AnalysisContextSchemaResolver.resolve(objectToResolve)
        expect:
        schema.getInlinePath().size() == 20
    }


    def "Check node #nodeName is  #type"() {
        given:
        def schema = AnalysisContextSchemaResolver.resolve(objectToResolve)
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
}
