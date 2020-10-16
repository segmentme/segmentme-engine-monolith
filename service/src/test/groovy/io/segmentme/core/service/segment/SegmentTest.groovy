package io.segmentme.core.service.segment

class SegmentTest extends BaseRuleTest {

    def "analyse segment #name - should be #isMatch"() {
        given:
        def segments = getSegment(name)
        and:
        def result = analysisService.analyze(context, segments)
        expect:
        def singleResult = resultValue(name, result)
        singleResult.value == isMatch
        singleResult.name == name
        where:
        name                                 | isMatch
        "USER EMAIL IN SEGMENT"              | true
        "EMAIL_NOT_IN"                       | false
        "AGE_GT"                             | true
        "AGE_GTE"                            | true
        "BIRTH_DATE_LT"                      | true
        "BIRTH_DATE_LTE"                     | true
        "REGISTERED_DATE_IN_RANGE"           | true
        "REGISTERED_DATE_NOT_IN_RANGE"       | false
        "POSTAL_CODE_CONTAINS_ANY"           | true
        "FIRST_POSTAL_CODE_CONTAINS_ONLY"    | true
        "NOT_FIRST_POSTAL_CODE_CONTAINS_ANY" | false
        "SECOND_PHONE_CONTAINS_ONLY"         | true
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | true
    }


    def "debug segment #name - should be #isMatch"() {
        given:
        def segment = getSegment(name).get(0)
        segment.setId(UUID.randomUUID().toString())
        and:
        def result = analysisService.debug(context, segment)
        expect:
        def singleResult = resultValue(name, result)
        singleResult.value == isMatch
        singleResult.name == name
        where:
        name                                 | isMatch
        "SECOND_PHONE_CONTAINS_ONLY_SEGMENT" | true
    }
}
