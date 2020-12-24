package io.segmentme.core.api.resource;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.SegmentExportRequest;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import io.segmentme.core.service.converter.SegmentShortInfoConverter;
import io.segmentme.core.service.dto.SegmentImportResult;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/segment")
public class SegmentController {

    private final SegmentManager segmentManager;

    public static final String SEGMENTS_FILE_NAME = "attachment; filename=segments.json";

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

    @PutMapping("/{segmentId}")
    @PreAuthorize("@segmentSecurityService.isManaged(#segmentId, #currentUser.id)")
    public void activate(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String segmentId, @RequestParam boolean isActive){
        log.info("Request to set active {} for segment with id {}", isActive, segmentId);
        segmentManager.activate(segmentId, isActive);
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
    @PreAuthorize("@segmentSecurityService.isManaged(#segmentId, #currentUser.id)")
    public SegmentDto findById(@AuthenticationPrincipal AuthUser currentUser,
                               @PathVariable String segmentId) {
        log.info("Request to find segment with id {}", segmentId);
        return segmentManager.findById(segmentId);
    }

    @DeleteMapping("/{segmentId}")
    @PreAuthorize("@segmentSecurityService.isManaged(#segmentId, #currentUser.id)")
    public void delete(@AuthenticationPrincipal AuthUser currentUser, @PathVariable String segmentId) {
        log.info("Request to delete segment with id {}", segmentId);
        segmentManager.delete(segmentId);
    }

    @PostMapping("/export")
    public void export(@RequestBody SegmentExportRequest exportRequest, HttpServletResponse response) throws IOException {
        response.setContentType(APPLICATION_JSON.toString());
        response.addHeader(CONTENT_DISPOSITION, SEGMENTS_FILE_NAME);
        segmentManager.exportSegments(exportRequest.getWorkspaceId(), exportRequest.getSegmentIds(), response.getOutputStream());
    }

    @PostMapping("/import/{contextId}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#contextId)")
    public SegmentImportResult importFile(@PathVariable String contextId, @RequestParam("file") MultipartFile file) throws IOException {
        return segmentManager.importSegments(contextId, file.getInputStream());
    }
}
