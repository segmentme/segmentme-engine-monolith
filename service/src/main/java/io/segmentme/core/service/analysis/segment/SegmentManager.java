package io.segmentme.core.service.analysis.segment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.service.context.ContextSchemaService;
import io.segmentme.core.db.service.segment.SegmentService;
import io.segmentme.core.db.service.workspace.WorkspaceService;
import io.segmentme.core.service.converter.SegmentConverter;
import io.segmentme.core.service.dto.SegmentImportResult;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import io.segmentme.core.service.exception.SegmentManagerException;
import io.segmentme.core.service.exception.error.SegmentMangerErrors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentManager {

    private final SegmentService segmentService;

    private final ContextSchemaService contextSchemaService;
    private final WorkspaceService workspaceService;

    private final ObjectMapper objectMapper;

    public SegmentDto save(SegmentDto rule, String contextId, String integrationPointKey) {
        Segment existedSegment = segmentService.findByIntegrationPointKeyAndKey(integrationPointKey, rule.getName());
        if (existedSegment != null && !Objects.equals(existedSegment.getId(), rule.getId())) {
            throw new SegmentManagerException(SegmentMangerErrors.DUPLICATED_SEGMENT_KEY);
        }

        Segment analysisRule = SegmentConverter.of(rule, contextId, integrationPointKey);

        return SegmentConverter.of(segmentService.create(analysisRule));
    }

    public List<SegmentDto> save(List<SegmentDto> rules, String contextId, String integrationPointKey) {

        List<Segment> analysisRules = rules.stream().map(it -> SegmentConverter.of(it, contextId, integrationPointKey))
            .collect(Collectors.toList());

        return segmentService.createAll(analysisRules).stream().map(SegmentConverter::of).collect(Collectors.toList());
    }

    public List<SegmentDto> findByIds(Iterable<String> ids) {
        return segmentService.findByIds(ids)
            .stream()
            .map(SegmentConverter::of)
            .collect(Collectors.toList());
    }

    public List<SegmentDto> findByIntegrationPointKey(String integrationPointKey) {
        return segmentService.findByIntegrationPointKey(integrationPointKey).stream()
            .map(SegmentConverter::of)
            .collect(Collectors.toList());
    }

    public List<SegmentDto> findByIntegrationPointKeys(Iterable<String> integrationPointKeys) {
        return segmentService.findByIntegrationPointKeys(integrationPointKeys)
            .stream()
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
        segmentService.save(byIntegrationPointKey);

    }

    public void unlinkFromContext(String contextId) {
        List<Segment> contextRules = segmentService.findByContextId(contextId);
        contextRules.forEach(it -> it.setContextId(null));
        segmentService.save(contextRules);
    }

    public SegmentImportResult importSegments(String contextId, InputStream outputStream) throws IOException {
        List<Segment> segments = objectMapper.readValue(outputStream, new TypeReference<List<Segment>>() {
        });

        ContextSchema targetContext = contextSchemaService.findById(contextId).get();
        SegmentImportResult result = new SegmentImportResult();
        List<String> created = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        Map<String, Segment> existedSegments = segmentService.findByContextId(contextId).stream().collect(Collectors.toMap(Segment::getName, it -> it));

        segments.forEach(it -> {
            it.setId(null);
            it.setContextId(contextId);
            it.setIntegrationPointKey(targetContext.getIntegrationPointKey());
            it.setCreatedBy(null);
            it.setLastModifiedBy(null);
            Segment existedSegment = existedSegments.get(it.getName());
            if (existedSegment != null) {
                it.setId(existedSegment.getId());
                updated.add(it.getName());
            } else {
                it.setCreatedDate(null);
                it.setLastModifiedDate(null);
                created.add(it.getName());
            }
        });
        result.setCreated(created);
        result.setUpdated(updated);
        segmentService.save(segments);
        return result;

    }

    public void exportSegments(String workspaceId, List<String> segmentIds, OutputStream outputStream) throws IOException {
        List<String> collect = workspaceService.findById(workspaceId).stream().map(Workspace::getIntegrationPoints).flatMap(Collection::stream).map(IntegrationPoint::getKey).collect(Collectors.toList());
        Iterable<Segment> segments = StreamSupport.stream(segmentService.findByIds(segmentIds).spliterator(), false).filter(it -> collect.contains(it.getIntegrationPointKey())).collect(Collectors.toList());

        objectMapper.writeValue(outputStream, segments);
    }
}
