package io.segmentme.core.service.dto.analysis;

import lombok.Data;

@Data
public class DebugResult {

    private String conditionId;

    private String conditionName;

    private boolean conditionMatchResult;

    private boolean matchResult;

    private String criteria;

    private String errorMessage;
}
