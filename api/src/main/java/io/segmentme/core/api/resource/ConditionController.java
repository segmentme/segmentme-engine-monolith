package io.segmentme.core.api.resource;

import io.segmentme.core.service.condition.ConditionManager;
import io.segmentme.core.service.dto.component.AbstractConditionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/condition")
public class ConditionController {

    private final ConditionManager conditionManager;

    @PostMapping("/{contextId}")
    public AbstractConditionDto<?> save(@PathVariable String contextId, @RequestBody @Valid AbstractConditionDto<?> conditions) {
        return conditionManager.create(conditions, contextId);
    }

    @GetMapping("/integrationPointKey")
    public List<AbstractConditionDto<?>> findByContextId(@PathVariable String contextId){
        return conditionManager.findByContextId(contextId);
    }

    @DeleteMapping("/{conditionId}")
    public void delete(@PathVariable String conditionId){
        conditionManager.delete(conditionId);
    }
}
