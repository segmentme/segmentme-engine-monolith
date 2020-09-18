package io.segmentme.core.service.rule;

import io.segmentme.core.db.repository.AbstractAnalysisRuleRepository;
import io.segmentme.core.service.dto.rule.AbstractAnalysisRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleManager {

    private final AbstractAnalysisRuleRepository analysisRuleRepository;

    public List<AbstractAnalysisRuleDto<?>> save(List<AbstractAnalysisRuleDto<?>> rules) {

        return null;
    }
}
