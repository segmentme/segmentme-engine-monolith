package io.segmentme.core.service.helper

import io.segmentme.core.db.domain.segment.Segment

class RuleHelper {

    static def fillRule(Object segment, Map args = [:]) {
        segment.aggregation = args['aggregation'] ?: Segment.AggregationType.AND
        segment.name = args['name'] ?: UUID.randomUUID().toString()
        segment.conditions = args['conditions']
        segment.matchResult = args['matchResult'] ?: true
        segment.embedded = args['embedded'] ?: false
        return segment
    }
}
