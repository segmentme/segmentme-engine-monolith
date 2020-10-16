package io.segmentme.core.api.resource;

import io.segmentme.core.service.analysis.condition.ConditionManager;
import io.segmentme.core.service.dto.analysis.conditions.AbstractConditionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/condition")
public class ConditionController {

    private final ConditionManager conditionManager;

    @PostMapping("/context/{contextId}")
    public AbstractConditionDto save(@PathVariable String contextId, @RequestBody @Valid AbstractConditionDto condition) {
        log.info("Request to create condition {} with contextId {}", condition, contextId);
        return conditionManager.create(condition, contextId);
    }

    @GetMapping("/context/{contextId}")
    public List<AbstractConditionDto> findByContextId(@PathVariable String contextId){
        log.info("Request to find conditions for contextId {}", contextId);
        return conditionManager.findByContextId(contextId);
    }

    @GetMapping("/{conditionId}")
    public AbstractConditionDto findByConditionId(@PathVariable String conditionId){
        log.info("Request to find conditions for conditionId {}", conditionId);
        return conditionManager.findByConditionId(conditionId);
    }


    @DeleteMapping("/{conditionId}")
    public void delete(@PathVariable String conditionId){
        log.info("Request to delete condition with id {}", conditionId);
        conditionManager.delete(conditionId);
    }
}
