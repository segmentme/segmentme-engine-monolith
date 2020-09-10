package io.segmentme.core.db.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalysisResult {

    private String ruleId;

    private List<String> names;

    private Object value;
}
