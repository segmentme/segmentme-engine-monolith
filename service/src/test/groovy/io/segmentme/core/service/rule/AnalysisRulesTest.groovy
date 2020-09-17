package io.segmentme.core.service.rule

import io.segmentme.core.db.dto.AnalysisResult

class AnalysisRulesTest extends BaseRuleTest {

    def "Boolean rule #flag - should be #isMatch"() {
        given:
        1 * analysisRuleRepository.findByPreconditionIdIsNull() >> getRule(flag)
        and:
        def result = analysisService.analyze(context)
        expect:
        resultValue(flag, result) == isMatch
        where:
        flag                                 | isMatch
        "EMAIL_IN"                           | true
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
        "ADDRESS_MINSK_ROLE_ADMIN_AND_MONEY" | true
        "PRECONDITION_WITH_TWO_RULES"        | true
    }


    def "Value rule #flag - should be #matchResult"() {
        given:
        1 * analysisRuleRepository.findByPreconditionIdIsNull() >> getRule(flag)
        and:
        def result = analysisService.analyze(context)
        expect:
        compareValue(flag, result, matchResult)
        where:
        flag                            | matchResult
        "EMAIL_IN_JSON"                 | "{\"key\":\"value\",\"object\":{\"propA\":\"A\",\"propB\":\"B\"}}"
        "CITY_MOSCOW_EXIST"             | "\"STRING_VALUE\""
        "UNKNOWN_FIELD_EXIST"           | "\"EXIST_UNKNOWN_VALUE\""
        "UNKNOWN_FIELD_NOT_EXIST"       | null
        "PRECONDITION_RULE_JSON"        | "[1,2,3,4,5]"
        "GROUP_RULE_JSON_RETURN_NUMBER" | "555"
    }

    def compareValue(String flag, List<AnalysisResult> results, Object matchResult) {
        return Objects.equals(Optional.ofNullable(resultValue(flag, results))
                .map(it -> it.toString())
                .orElse(null), matchResult)
    }
}
