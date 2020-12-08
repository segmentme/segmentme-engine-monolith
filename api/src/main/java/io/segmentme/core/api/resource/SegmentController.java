package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.converter.SegmentShortInfoConverter;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/segment")
public class SegmentController {

    private final SegmentManager segmentManager;

    @PostMapping("/context/{contextId}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#contextId)")
    public SegmentDto save(@PathVariable String contextId, @RequestParam(required = false) String integrationPointKey, @RequestBody @Valid SegmentDto rule) {
        log.info("Request to create rule {} with contextId {}", rule, contextId);
        return segmentManager.save(rule, contextId, integrationPointKey);
    }

    @GetMapping("/context/{contextId}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#contextId)")
    public List<SegmentDto> findByContextId(@PathVariable String contextId) {
        log.info("Request to find rule for contextId {}", contextId);
        return segmentManager.findByContextId(contextId);
    }

    @PostMapping
    @PreAuthorize("@securityService.isValidIntegrationPointKeys(#integrationPointKeys, #currentUser.id)")
    public List<?> findByIntegrationPointKeys(@AuthenticationPrincipal AuthUser currentUser,
                                              @RequestParam(required = false, defaultValue = "false") boolean shortForm,
                                              @RequestBody @Valid @NotEmpty List<String> integrationPointKeys) {
        log.info("Request to find rule for integrationPointKeys {}", integrationPointKeys);
        List<SegmentDto> segments = segmentManager.findByIntegrationPointKeys(integrationPointKeys);
        return shortForm ? segments.stream().map(SegmentShortInfoConverter::of).collect(Collectors.toList()) : segments;
    }

    @GetMapping("/{segmentId}")
    public SegmentDto findById(@PathVariable String segmentId) {
        log.info("Request to find segment with id {}", segmentId);
        return segmentManager.findById(segmentId);
    }

    @DeleteMapping("/{segmentId}")
    public void delete(@PathVariable String segmentId) {
        log.info("Request to delete segment with id {}", segmentId);
        segmentManager.delete(segmentId);
    }
}
