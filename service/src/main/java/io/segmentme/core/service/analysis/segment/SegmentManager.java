package io.segmentme.core.service.analysis.segment;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.service.segment.SegmentService;
import io.segmentme.core.service.converter.SegmentConverter;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentManager {

    private final SegmentService segmentService;

    public SegmentDto save(SegmentDto rule, String contextId, String integrationPointKey) {
        Segment analysisRule = SegmentConverter.of(rule, contextId, integrationPointKey);
        return SegmentConverter.of(segmentService.create(analysisRule));
    }

    public List<SegmentDto> save(List<SegmentDto> rules, String contextId, String integrationPointKey) {

        List<Segment> analysisRules = rules.stream().map(it -> SegmentConverter.of(it, contextId, integrationPointKey))
                .collect(Collectors.toList());

        return segmentService.createAll(analysisRules).stream().map(SegmentConverter::of).collect(Collectors.toList());
    }

    public List<SegmentDto> findByIntegrationPointKey(String integrationPointKey) {
        return segmentService.findByIntegrationPointKey(integrationPointKey).stream()
                .map(SegmentConverter::of)
                .collect(Collectors.toList());
    }

    public List<SegmentDto> findByContextId(String contextId) {
        return segmentService.findByContextId(contextId).stream().map(SegmentConverter::of).collect(Collectors.toList());
    }

    public SegmentDto findById(String segmentId) {
        return segmentService.findById(segmentId).map(SegmentConverter::of).get();
    }

    public void delete(String ruleId) {
        segmentService.deleteById(ruleId);
    }

    public void unlinkFromIntegrationPoint(String integrationPointKey) {
        List<Segment> byIntegrationPointKey = segmentService.findByIntegrationPointKey(integrationPointKey);
        byIntegrationPointKey.forEach(it -> it.setIntegrationPointKey(null));
        segmentService.update(byIntegrationPointKey);

    }

    public void unlinkFromContext(String contextId) {
        List<Segment> contextRules = segmentService.findByContextId(contextId);
        contextRules.forEach(it -> it.setContextId(null));
        segmentService.update(contextRules);
    }
}
