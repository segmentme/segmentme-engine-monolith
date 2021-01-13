package io.segmentme.core.service.analysis.state;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.service.state.StateService;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.dto.analysis.AnalysisData;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateAnalysisService {

    private final StateService stateService;

    private final AnalysisService analysisService;

    public List<StateAnalysisResult> analyse(String integrationPointKey, AnalysisData analysisData) {
        List<State> sates = stateService.findByIntegrationPointKey(integrationPointKey);
        return sates.stream()
            .map(it -> StateAnalysisResult.of(it.getName(), isMatched(it, analysisData) ? it.getValue() : it.getDefaultValue()))
            .collect(Collectors.toList());
    }

    private boolean isMatched(State state, AnalysisData analysisData) {
        return analysisService.analyze(state.getIntegrationPointKey(), analysisData, state.getSegment())
            .stream()
            .findFirst()
            .map(SegmentAnalysisResult::isValue)
            .orElse(false);
    }
}
