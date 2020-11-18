package io.segmentme.core.db.domain.statistic;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "statistic")
@Data
public class StatisticLog extends DbObject {
    private long analysisTime;

    private String integrationPointKey;

    private int executedConditionsCount;

    private List<ConditionStatistic> conditionStatistics;

    private List<SegmentStatistic> segmentStatistics;

    private Map<String, ContextSchema.InlineType> knownTypes;

    private Map<String, Object> nodeValues;

    private String workspaceId;

    @Data
    public static class SegmentStatistic {
        private String id;

        private String hash;

        private Map<String, Integer> conditionsHash;

        private boolean result;
    }

    @Data
    public static class ConditionStatistic {
        private String hash;
        private String criteria;
        private String errors;
    }
}
