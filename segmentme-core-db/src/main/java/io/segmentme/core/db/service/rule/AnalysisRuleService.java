package io.segmentme.core.db.service.rule;

import io.segmentme.core.db.domain.context.AnalysisContext;
import io.segmentme.core.db.domain.rule.*;
import io.segmentme.core.db.repository.AnalysisRuleRepository;
import io.segmentme.core.db.repository.PreconditionAnalysisRuleRepository;
import io.segmentme.core.db.service.condition.ConditionMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisRuleService {

    private final AnalysisRuleRepository analysisRuleRepository;

    private final PreconditionAnalysisRuleRepository preconditionAnalysisRuleRepository;

    public Object analyze(AnalysisContext context){

        //TODO need to find rules in db by params... user_id or other key
        List<SimpleAnalysisRule<?>> rules = analysisRuleRepository.findAll();

        List<PreconditionAnalysisRule> all = preconditionAnalysisRuleRepository.findAll();


        if (CollectionUtils.isEmpty(rules)) {
            return Arrays.asList();
        }


        return null;

    }
}
