package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.*;
import io.segmentme.core.db.repository.AnalyzedDataRepository;
import io.segmentme.core.db.repository.StatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

@Service
@RequiredArgsConstructor
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {


    private final MongoTemplate mongoTemplate;

    private final AnalyzedDataRepository analyzedDataRepository;


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

    public List<AggregatedAnalysisCount> getAnalysisCountForCriteriaValue(String workspaceId, int period, String criteria, String value) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        String criteriaField = "nodeValues." + criteria.replaceAll("\\.", "#");

        List<Object> values = resolvePossibleValueType(value);
        MatchOperation dateFilter = Aggregation.match(new Criteria("workspaceId").is(workspaceId).and("lastModifiedDate").gte(localDateTime));

        MatchOperation nodeValuePath = Aggregation.match(new Criteria(criteriaField).in(values));

        ProjectionOperation analyzedDataProjection = Aggregation.project("hash");

        TypedAggregation<AnalyzedData> analyzedDataAggregation
                = new TypedAggregation<>(AnalyzedData.class, dateFilter, nodeValuePath, analyzedDataProjection);
        AggregationResults<AnalyzedData> analyzedDataList = mongoTemplate.aggregate(analyzedDataAggregation, AnalyzedData.class);

        dateFilter = Aggregation.match(new Criteria("workspaceId").is(workspaceId).and("createdDate").gte(localDateTime));
        MatchOperation analyzedDataMatch = Aggregation.match(new Criteria("analyzedDataKey").in(analyzedDataList.getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList())));
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "createdDate");

        ProjectionOperation projectStage = Aggregation
                .project("workspaceId", "createdDate", "analysisTime", "integrationPointKey")
                .and("createdDate").dateAsFormattedString("%Y-%m-%dT%H:00:00").as("dateHour");

        ProjectionOperation finalProjections = Aggregation.project("count", "totalAnalysisTime")
                .and("_id.dateHour").as("dateTime")
                .and("_id.integrationPointKey").as("integrationPointKey");

        GroupOperation groupOperation = group("dateHour", "integrationPointKey").count().as("count")
                .sum("analysisTime").as("totalAnalysisTime");

        TypedAggregation<StatisticLog> aggregation
                = new TypedAggregation<>(StatisticLog.class, dateFilter, sort, analyzedDataMatch, projectStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();
    }

    public Page<StatisticLog> getSegmentStatistic(String workspaceId, LocalDateTime start, LocalDateTime end, String criteria, String value, Pageable pageable) {
        Instant startDate = start.toInstant(ZoneOffset.UTC);
        Instant endDate = end.toInstant(ZoneOffset.UTC);

        String criteriaField = "nodeValues." + criteria.replaceAll("\\.", "#");

        List<Object> values = resolvePossibleValueType(value);
        MatchOperation dateFilter = Aggregation.match(new Criteria("workspaceId").is(workspaceId)
                .and("lastModifiedDate").gte(startDate).lte(endDate));

        MatchOperation nodeValuePath = Aggregation.match(new Criteria(criteriaField).in(values));

        ProjectionOperation analyzedDataProjection = Aggregation.project("hash");

        TypedAggregation<AnalyzedData> analyzedDataAggregation
                = new TypedAggregation<>(AnalyzedData.class, dateFilter, nodeValuePath, analyzedDataProjection);

        List<String> analyzedDataHashes = mongoTemplate.aggregate(analyzedDataAggregation, AnalyzedData.class)
                .getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList());

        dateFilter = Aggregation.match(new Criteria("workspaceId").is(workspaceId)
                .and("createdDate").gte(startDate).lt(endDate));

        MatchOperation analyzedDataMatch = Aggregation.match(new Criteria("analyzedDataKey").in(analyzedDataHashes));

        SkipOperation skipOperation = new SkipOperation(pageable.getPageNumber() * pageable.getPageSize());
        LimitOperation limit = Aggregation.limit(pageable.getPageSize());
        CountOperation totalCountOperator = Aggregation.count().as("count");

        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "createdDate");
        TypedAggregation<StatisticLog> countAggregation = new TypedAggregation<>(StatisticLog.class, dateFilter, sort, analyzedDataMatch, totalCountOperator);
        TypedAggregation<StatisticLog> typedAggregation = new TypedAggregation<>(StatisticLog.class, dateFilter, sort, analyzedDataMatch, skipOperation, limit);

        List<StatisticLog> result = mongoTemplate.aggregate(typedAggregation, StatisticLog.class).getMappedResults();

        var count = Optional.of(mongoTemplate.aggregate(countAggregation, TotalCount.class))
                .map(AggregationResults::getUniqueMappedResult)
                .map(TotalCount::getCount)
                .orElse(0L);

        return new PageImpl<>(result, pageable, count);
    }

    private List<Object> resolvePossibleValueType(String value) {
        List<Object> values = new ArrayList<>();
        values.add(value);
        try {
            values.add(Integer.valueOf(value));
        } catch (Throwable ex) {
            //mute
        }
        try {
            values.add(Double.valueOf(value));
        } catch (Throwable ex) {
            //mute
        }
        try {
            Optional.ofNullable(BooleanUtils.toBooleanObject(value)).ifPresent(values::add);
        } catch (Throwable ex) {
            //mute
        }


        return values;
    }

    public ExploreStatisticLog getStatisticLogOverview(String statisticLogId) {
        return repository.findById(statisticLogId).map(log -> {
            AnalyzedData hash = analyzedDataRepository.findByHash(log.getAnalyzedDataKey());
            return new ExploreStatisticLog().setRawPayload(hash.getPayload());
        }).orElseGet(ExploreStatisticLog::new);
    }

    @Data
    public static class ExploreStatisticLog {
        private String rawPayload;
    }

    @Data
    public static class TotalCount {
        private Long count;
    }
}
