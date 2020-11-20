package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.AggregatedAnalysisCount;
import io.segmentme.core.db.domain.statistic.SegmentStatisticCount;
import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.repository.StatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
@RequiredArgsConstructor
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {

    private final MongoTemplate mongoTemplate;

    public List<SegmentStatisticCount> getSegmentStatistic(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        MatchOperation dateFilter = Aggregation
            .match(new Criteria("workspaceId")
                .is(workspaceId)
                .and("createdDate")
                .gte(localDateTime));
        UnwindOperation unwind = Aggregation.unwind("segmentStatistics");


        ProjectionOperation segmentInfo = Aggregation
            .project("workspaceId")
            .and("segmentStatistics.segmentId").as("segmentId")
            .and("segmentStatistics.result").as("segmentResult");

        MatchOperation trueSegmentFilter = Aggregation.match(new Criteria("segmentResult").is(true));
        GroupOperation groupBySegment = group("segmentId").count().as("count");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "count");

        ProjectionOperation segmentCountProjection = Aggregation.project("count").and("_id").as("segmentId");

        TypedAggregation<StatisticLog> aggregation
            = new TypedAggregation<>(StatisticLog.class, dateFilter, unwind, segmentInfo, trueSegmentFilter, groupBySegment, sortOperation, segmentCountProjection);

        AggregationResults<SegmentStatisticCount> result = mongoTemplate.aggregate(aggregation, SegmentStatisticCount.class);

        return result.getMappedResults();

    }

    public List<AggregatedAnalysisCount> getAnalysisCount(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);

        ProjectionOperation projectStage = Aggregation
            .project("workspaceId", "createdDate", "analysisTime", "integrationPointKey")
            .and("createdDate").dateAsFormattedString("%Y-%m-%dT%H:00:00").as("dateHour");

        MatchOperation matchStage = Aggregation
            .match(new Criteria("workspaceId")
                .is(workspaceId)
                .and("createdDate")
                .gte(localDateTime));

        GroupOperation groupOperation = group("dateHour", "integrationPointKey").count().as("count").sum("analysisTime").as("totalAnalysisTime");


        ProjectionOperation finalProjections = Aggregation.project("count", "totalAnalysisTime").and("_id.dateHour").as("dateTime").and("_id.integrationPointKey").as("integrationPointKey");

        TypedAggregation<StatisticLog> aggregation
            = new TypedAggregation<>(StatisticLog.class, projectStage, matchStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();

    }


}
