package io.segmentme.core.db.common

import io.segmentme.core.db.domain.rule.AggregationType
import io.segmentme.core.db.domain.rule.BooleanAnalysisRule
import io.segmentme.core.db.repository.AnalysisRuleRepository
import org.springframework.beans.factory.annotation.Autowired

class Test extends BaseDatabaseTest {

    @Autowired
    private AnalysisRuleRepository analysisRuleRepository

    def 'testMongoConnection'() {
        given:
        def modelToSave = new BooleanAnalysisRule().setAggregation(AggregationType.AND)
        when:
        def savedModel = analysisRuleRepository.save(modelToSave)
        then:
        def existedModel = analysisRuleRepository.findById(savedModel.getId())
        existedModel != null
    }
}
