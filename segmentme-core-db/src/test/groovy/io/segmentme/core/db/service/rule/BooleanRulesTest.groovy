package io.segmentme.core.db.service.rule

class BooleanRulesTest extends BaseRuleTest {

    def "analise boolean rules"() {
        given:
        def result = analyse(flag)
        expect:
        resultValue(flag, result) == isMatch
        where:
        flag                              | isMatch
        "EMAIL_IN"                        | true
        "EMAIL_NOT_IN"                    | false
        "AGE_GT"                          | true
        "AGE_GTE"                         | true
        "BIRTH_DATE_LT"                   | true
        "BIRTH_DATE_LTE"                  | true
        "REGISTERED_DATE_IN_RANGE"        | true
        "REGISTERED_DATE_NOT_IN_RANGE"    | false
    }


    def analyse(String flagName) {
        saveRule(flagName)
        def analyze = analysisService.analyze(context)
        return analyze
    }
}
