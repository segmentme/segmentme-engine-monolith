package io.segmentme.core.api.resource;

import io.segmentme.core.service.dto.rule.AbstractAnalysisRuleDto;
import io.segmentme.core.service.rule.RuleManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class AnalysisRuleController {

    private final RuleManager ruleManager;

    @PostMapping("/{contextId}")
    public AbstractAnalysisRuleDto<?> save(@PathVariable String contextId, @RequestBody @Valid AbstractAnalysisRuleDto<?> rule) {
        log.info("Request to create rule {} with contextId {}", rule, contextId);

        //TODO integration entry point shouldn't be null

        return ruleManager.save(rule, contextId, null);
    }

    @GetMapping("/{contextId}")
    public List<AbstractAnalysisRuleDto<?>> findByContextId(@PathVariable String contextId){
        log.info("Request to find rule for contextId {}", contextId);
        return ruleManager.findByContextId(contextId);
    }

    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable String ruleId){
        log.info("Request to delete rule with id {}", ruleId);
        ruleManager.delete(ruleId);
    }
}
