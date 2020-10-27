package io.segmentme.core.api.resource;

import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import io.segmentme.core.service.analysis.segment.SegmentManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/segment")
public class SegmentController {

    private final SegmentManager segmentManager;

    @PostMapping("/{contextId}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#contextId)")
    public SegmentDto save(@PathVariable String contextId, @RequestBody @Valid SegmentDto rule) {
        log.info("Request to create rule {} with contextId {}", rule, contextId);

        //TODO integration entry point shouldn't be null

        return segmentManager.save(rule, contextId, null);
    }

    @GetMapping("/{contextId}")
    @PreAuthorize("@contextSchemaSecurityService.isManagedSchema(#contextId)")
    public List<SegmentDto> findByContextId(@PathVariable String contextId){
        log.info("Request to find rule for contextId {}", contextId);
        return segmentManager.findByContextId(contextId);
    }

    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable String ruleId){
        log.info("Request to delete rule with id {}", ruleId);
        segmentManager.delete(ruleId);
    }
}
