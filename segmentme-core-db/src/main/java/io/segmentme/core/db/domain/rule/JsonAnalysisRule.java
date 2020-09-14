package io.segmentme.core.db.domain.rule;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JsonAnalysisRule extends SimpleAnalysisRule<JsonNode> {
}

