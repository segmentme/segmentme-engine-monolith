package io.segmentme.core.db.service.rule;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.domain.rule.PreconditionAnalysisRule;
import io.segmentme.core.db.domain.rule.SimpleAnalysisRule;
import io.segmentme.core.db.dto.AnalysisResult;
import io.segmentme.core.db.repository.PreconditionAnalysisRuleRepository;
import io.segmentme.core.db.repository.SimpleAnalysisRuleRepository;
import io.segmentme.core.db.service.rule.common.AnalysisRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static io.segmentme.core.db.domain.rule.AbstractAnalysisRule.RuleType.PRECONDITION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRuleService analysisRuleService;

    private final SimpleAnalysisRuleRepository simpleAnalysisRuleRepository;

    private final PreconditionAnalysisRuleRepository preconditionAnalysisRuleRepository;

    public List<AnalysisResult> analyze(AnalysisContextSchema context) {

        //TODO need to find rules in db by params... user_id or other key

        List<PreconditionAnalysisRule> group = preconditionAnalysisRuleRepository.findAll();

        List<AnalysisResult> processedPreconditions = analysisRuleService.analyze(group, context, PRECONDITION);

        List<SimpleAnalysisRule<?>> rules = simpleAnalysisRuleRepository.findAll();

        List<String> processedRuleIds = processedPreconditions.stream().map(AnalysisResult::getRuleId).collect(Collectors.toList());

        rules.removeIf(it -> processedRuleIds.contains(it.getId()));

        List<AnalysisResult> processedRules = rules.stream().map(it -> analysisRuleService.analyze(it, context)).collect(Collectors.toList());

        processedPreconditions.addAll(processedRules);

        return processedPreconditions;
    }
}
