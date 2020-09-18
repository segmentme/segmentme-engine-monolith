package io.segmentme.core.service.dto.rule;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JsonAnalysisRuleDto extends SimpleAnalysisRuleDto<JsonNode> {
}
