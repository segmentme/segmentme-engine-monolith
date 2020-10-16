package io.segmentme.core.service.analysis.segment


import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.dto.analysis.conditions.SingleConditionDto
import io.segmentme.core.service.dto.analysis.segment.SegmentDto
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.db.domain.condition.AbstractCondition.ConditionType.LT
import static io.segmentme.core.service.helper.ConditionHelper.fillCondition
import static io.segmentme.core.service.helper.RuleHelper.fillRule

class RuleManagerTest extends BaseTestWithContext {

    private static final ObjectMapper MAPPER = new ObjectMapper()

    @Autowired
    private SegmentManager segmentManager

    def "Analysis rule converting:"() {
        given:
        def integrationPointKey = UUID.randomUUID().toString()
        def contextId = UUID.randomUUID().toString()
        def source = fillRule(new SegmentDto(), values)
        segmentManager.save(List.of(source), contextId, integrationPointKey)

        expect:
        def existedRules = segmentManager.findByIntegrationPointKey(integrationPointKey)
        existedRules.size() == 1
        def rule = existedRules[0]
        rule.matchResult == source.matchResult
        rule.embedded == source.embedded
        rule.aggregation == source.aggregation
        rule.conditions.size() == 1
        def convertedCondition = rule.conditions[0]
        def condition = source.conditions[0]
        where:
        values                                                                                       | _
        ['matchResult': true, 'conditions': List.of(fillCondition(new SingleConditionDto(), 1, LT))] | _
    }
}
