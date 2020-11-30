package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.context.DbObject;
import io.segmentme.core.db.domain.statistic.*;
import io.segmentme.core.db.repository.StatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
@RequiredArgsConstructor
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {

    private final static String NODE_VALUE_PATH_ALIAS = "criteriaField";

    private final static String DB_STATISTIC_COLLECTION_NAME = "statistic";

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

    public List<StatisticLog> getSegmentStatistic(String workspaceId, int period, String criteria, String value) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        String criteriaField = "nodeValues." + criteria.replaceAll("\\.", "#");

        MatchOperation dateFilter = Aggregation.match(new Criteria("workspaceId").is(workspaceId).and("createdDate").gte(localDateTime));

        MatchOperation nodeValuePath = Aggregation.match(new Criteria(NODE_VALUE_PATH_ALIAS).in(value));

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "createdDate");

        ProjectionOperation nodeValuePathProjection = Aggregation.project("id").and(criteriaField).as(NODE_VALUE_PATH_ALIAS);

        UnwindOperation nodeValuePathUnwind = Aggregation.unwind(NODE_VALUE_PATH_ALIAS);

        ProjectionOperation nodeValuePathAsString = Aggregation.project("id")
                .andExpression("toString(" + NODE_VALUE_PATH_ALIAS + ")")
                .as(NODE_VALUE_PATH_ALIAS);

        Aggregation aggregation = Aggregation.newAggregation(dateFilter, sort, nodeValuePathProjection, nodeValuePathUnwind, nodeValuePathAsString, nodeValuePath);

        List<String> ids = mongoTemplate.aggregate(aggregation, DB_STATISTIC_COLLECTION_NAME, StatisticLog.class)
                .getMappedResults().stream().map(DbObject::getId).distinct().collect(Collectors.toList());

        TypedAggregation<StatisticLog> typedAggregation = new TypedAggregation<>(StatisticLog.class, Aggregation.match(Criteria.where("id").in(ids)));

        AggregationResults<StatisticLog> result = mongoTemplate.aggregate(typedAggregation, StatisticLog.class);

        return result.getMappedResults();
    }
}
