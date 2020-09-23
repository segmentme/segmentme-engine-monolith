package io.segmentme.core.api.resource;

import io.segmentme.core.service.dto.rule.AbstractAnalysisRuleDto;
import io.segmentme.core.service.rule.RuleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class AnalysisRuleController {

    private final RuleManager ruleManager;

    @PostMapping("/{contextId}/{integrationPointKey}")
    public List<AbstractAnalysisRuleDto<?>> save(@PathVariable String contextId, @PathVariable String integrationPointKey, @Valid List<AbstractAnalysisRuleDto<?>> rules) {
        return ruleManager.save(rules, contextId, integrationPointKey);
    }

    @GetMapping("/{integrationPointKey}")
    public List<AbstractAnalysisRuleDto<?>> findByIntegrationPointKey(@PathVariable String integrationPointKey){
        return ruleManager.findByIntegrationPointKey(integrationPointKey);
    }

    @DeleteMapping("/{ruleId}")
    public void delete(@PathVariable String ruleId){
        ruleManager.delete(ruleId);
    }
}
