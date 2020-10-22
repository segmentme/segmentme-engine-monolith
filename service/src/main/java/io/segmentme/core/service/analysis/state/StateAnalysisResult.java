package io.segmentme.core.service.analysis.state;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class StateAnalysisResult {

    private String name;

    private JsonNode value;
}
