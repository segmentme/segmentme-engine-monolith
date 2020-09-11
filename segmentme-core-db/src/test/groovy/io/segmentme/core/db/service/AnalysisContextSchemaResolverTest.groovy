package io.segmentme.core.db.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class AnalysisContextSchemaResolverTest extends Specification {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode objectToResolve = null;

    def setup() {
        objectToResolve = objectMapper.readValue("{\n" +
                "    \"user\": {\n" +
                "        \"numbersArray\":[12,23,22.4],\n" +
                "        \"email\": \"vladislavkondratenko@coherentsolutions.com\",\n" +
                "        \"firstName\": \"Vladislav\",\n" +
                "        \"lastName\": \"Kondratenko \",\n" +
                "        \"userDetails\": {\n" +
                "            \"gender\": \"\",\n" +
                "            \"address\": {\n" +
                "                \"addressLine1\": \"Dasdsadas\",\n" +
                "                \"state\": \"NU\",\n" +
                "                \"city\": \"Dads dada\",\n" +
                "                \"zipCode\": \"22334\"\n" +
                "            },\n" +
                "            \"birthDate\": \"2006-10-22\",\n" +
                "            \"phone\": \"213123\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"stringArray\":[\"11\",\"44\"],\n" +
                "    \"objectArrays\": [\n" +
                "        {\n" +
                "            \"id\": \"02af8297654248b5a3d17b0f173fa581\",\n" +
                "            \"agreementNumber\": \"23872\",\n" +
                "            \"isActive\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"02af8297654248b5a3d17b0f173fa581\",\n" +
                "          \"agreementNumber\": \"23872\",\n" +
                "          \"isActive\": true,\n" +
                "          \"dateTime\": \"2010-01-01T12:00:00Z\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"status\": \"ACTIVE\",\n" +
                "    \"fullAge\": 12,\n" +
                "    \"weight\":199999999.123232\n" +
                "}", JsonNode.class)
    }


    def "Resolve"() {
        when:
        def resolve = AnalysisContextSchemaResolver.resolve(objectToResolve)
        then:
        resolve != null

    }
}
