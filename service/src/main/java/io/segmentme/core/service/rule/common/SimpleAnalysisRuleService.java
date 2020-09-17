package io.segmentme.core.service.rule.common;

import io.segmentme.core.db.domain.rule.SimpleAnalysisRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
abstract class SimpleAnalysisRuleService<A extends SimpleAnalysisRule<?>> extends AbstractAnalysisRuleService<A> {

    @Override
    List<String> getNames(A rule) {
        return rule.getFlags();
    }
}
