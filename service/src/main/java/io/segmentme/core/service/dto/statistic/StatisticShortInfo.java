package io.segmentme.core.service.dto.statistic;

import lombok.Data;

import java.time.Instant;

@Data
public class StatisticShortInfo {

    private String id;

    private String workspaceId;

    private Instant dateTime;

    private long analysisTime;

    private String integrationPointKey;

    private int segmentCount;

}
