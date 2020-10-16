package io.segmentme.core.service.dto.analysis;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;

@Data
public class DebugResult {

    private String conditionId;

    private String conditionName;

    private boolean conditionMatchResult;

    private boolean finalMatchResult;

    private String criteria;

    private String segmentId;

    private String segmentName;

    private String errorMessage;

    private ContextSchema.InlineType criteriaType;

    private Object value;

}
