package io.segmentme.core.service.service.context


import io.segmentme.core.service.analysis.ContextPreprocessorServiceImpl
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.core.service.context.ContextSchemaResolver
import io.segmentme.core.service.workspace.UserConfigurationServiceImpl
import spock.lang.Specification

import java.time.*
import java.time.format.DateTimeFormatter

import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT

class ContextPreprocessorServiceImplTest extends Specification {

    static ResourceHolder resourceHolder = new ResourceHolder();

    static schema = null

    def setupSpec() {
        resourceHolder.init();
        schema = new ContextSchemaResolver(new UserConfigurationServiceImpl()).resolve(resourceHolder.getValidJsonPayloadConfiguration())
    }

    def "Parsed context should have correct keys size"() {
        given:
        def json = resourceHolder.getValidJsonPayloadConfiguration()
        when:
        def result = new ContextPreprocessorServiceImpl(new UserConfigurationServiceImpl()).prepareContext(json, schema)
        then:
        result.getValues().size() == 19
    }

    def "Value of #criteria  should be #expectedValue"() {
        given:
        def json = resourceHolder.getValidJsonPayloadConfiguration()
        def result = new ContextPreprocessorServiceImpl(new UserConfigurationServiceImpl()).prepareContext(json, schema)
        expect:
        def value = result.getValues().get(criteria)
        assert value == expectedValue
        where:
        criteria                              || expectedValue
        "user.email"                          || "vladislavkondratenko@coherentsolutions.com"
        "user.name"                           || "Vladislav"
        "user.details.gender"                 || ""
        "user.numbersArray"                   || [12, 23, 22.4]
        "user.details.address.addressLine1"   || "Dasdsadas"
        "user.details.address.state"          || "NU"
        "user.details.birthDate"              || Instant.from(ZonedDateTime.of(LocalDate.parse("2006-10-22"), LocalTime.MIDNIGHT, ZoneId.systemDefault()))
        "user.details.phone"                  || "213123"
        "user.status"                         || "ACTIVE"
        "user.fullAge"                        || 12
        "user.weight"                         || 199999999.123232
        "stringArray"                         || ["11", "44"]
        "objectArrays.id"                     || ["123", "431"]
        "objectArrays.agreementNumber"        || [123]
        "objectArrays.isActive"               || [true, false]
        "objectArrays.dateTime"               || [Instant.from(DateTimeFormatter.ofPattern(ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'").withZone(ZoneId.systemDefault()).parse("2010-01-01T12:00:13Z"))]
        "objectArrays.subObjects.subObjectId" || [["id1", "id2"], ["id3"]]
        "objectArrays.subObjects.array"       || [[["a1", "a2", "a3"], ["a4", "a5"]], [["a6"]]]
        "objectArrays.numbersArray"           || [[1, 2, 3], [12, 23, 22.4]]
        "unknownvalue"                        || null
    }
}
