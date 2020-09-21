package io.segmentme.core.service.rule;

import io.segmentme.core.db.domain.rule.AbstractAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository;
import io.segmentme.core.service.analysis.ContextValueHolder;
import io.segmentme.core.service.rule.common.AnalysisRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRuleService analysisRuleService;

    private final AbstractAnalysisRuleRepository analysisRuleRepository;

    public List<AnalysisResult> analyze(ContextValueHolder context) {

        //TODO need to find rules in db by params... user_id or other key
        List<AbstractAnalysisRule<?>> group = analysisRuleRepository.findByPreconditionIdIsNull();

        return group.stream().map(it -> analysisRuleService.analyze(it, context))
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
    }
}
