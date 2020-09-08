package io.segmentme.core.db

import io.segmentme.core.db.common.BaseSpecificationWebMvc

class Test extends BaseSpecificationWebMvc {

    def 'hello'() {
        given:
        def a = 1
        when:
        a = a * 3
        then:
        a == 3
    }

    def 'hello1'() {
        given:
        def a = 1
        when:
        a = a * 3
        then:
        a == 3
    }

    def 'hello2'() {
        given:
        def a = 1
        when:
        a = a * 3
        then:
        a == 3
    }
}
