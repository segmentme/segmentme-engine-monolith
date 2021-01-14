package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.AggregatedAnalysisCount;
import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.domain.statistic.SegmentStatisticCount;
import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.repository.AnalyzedDataRepository;
import io.segmentme.core.db.repository.StatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {



    private final MongoTemplate mongoTemplate;

    private final AnalyzedDataRepository analyzedDataRepository;


    public List<SegmentStatisticCount> getSegmentStatistic(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        MatchOperation dateFilter = match(new Criteria(PROP_WORKSPACE_ID)
                .is(workspaceId)
                .and(CREATED_DATE)
                .gte(localDateTime));
        UnwindOperation unwind = Aggregation.unwind(PROP_SEGMENT_STATISTICS);


        ProjectionOperation segmentInfo = Aggregation
                .project(PROP_WORKSPACE_ID)
                .and(SEGMENT_STATISTICS_SEGMENT_ID).as(SEGMENT_ID)
                .and(SEGMENT_STATISTICS_RESULT).as(SEGMENT_RESULT);

        MatchOperation trueSegmentFilter = match(new Criteria(SEGMENT_RESULT).is(true));
        GroupOperation groupBySegment = group(SEGMENT_ID).count().as(COUNT);
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, COUNT);

        ProjectionOperation segmentCountProjection = Aggregation.project(COUNT).and(ID).as(SEGMENT_ID);

        TypedAggregation<StatisticLog> aggregation
                = new TypedAggregation<>(StatisticLog.class, dateFilter, unwind, segmentInfo, trueSegmentFilter, groupBySegment, sortOperation, segmentCountProjection);

        AggregationResults<SegmentStatisticCount> result = mongoTemplate.aggregate(aggregation, SegmentStatisticCount.class);

        return result.getMappedResults();

    }

    public List<AggregatedAnalysisCount> getAnalysisCount(String workspaceId, int period) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);

        ProjectionOperation projectStage = Aggregation
                .project(PROP_WORKSPACE_ID, CREATED_DATE, ANALYSIS_TIME, INTEGRATION_POINT_KEY)
                .and(CREATED_DATE).dateAsFormattedString(DATE_FORMATTER).as(DATE_HOUR);

        MatchOperation matchStage = match(new Criteria(PROP_WORKSPACE_ID)
                .is(workspaceId)
                .and(CREATED_DATE)
                .gte(localDateTime));

        GroupOperation groupOperation = group(DATE_HOUR, INTEGRATION_POINT_KEY).count()
            .as(COUNT).sum(ANALYSIS_TIME).as(TOTAL_ANALYSIS_TIME);


        ProjectionOperation finalProjections = Aggregation.project(COUNT, TOTAL_ANALYSIS_TIME)
            .and(ID_DATE_HOUR).as(DATE_TIME).and(ID_INTEGRATION_POINT_KEY).as(INTEGRATION_POINT_KEY);

        TypedAggregation<StatisticLog> aggregation
                = new TypedAggregation<>(StatisticLog.class, projectStage, matchStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();

    }

    public List<AggregatedAnalysisCount> getAnalysisCountForCriteriaValue(String workspaceId, int period, String criteria, String value) {
        LocalDateTime localDateTime = LocalDate.now().minus(period, ChronoUnit.DAYS).atTime(LocalTime.MIDNIGHT);
        String criteriaField = convertToNodeValues(criteria);

        List<Object> values = resolvePossibleValueType(value);
        MatchOperation dateFilter = match(new Criteria(PROP_WORKSPACE_ID).is(workspaceId).and(LAST_MODIFIED_DATE).gte(localDateTime));

        MatchOperation nodeValuePath = match(new Criteria(criteriaField).in(values));

        ProjectionOperation analyzedDataProjection = Aggregation.project(HASH);

        TypedAggregation<AnalyzedData> analyzedDataAggregation
                = new TypedAggregation<>(AnalyzedData.class, dateFilter, nodeValuePath, analyzedDataProjection);
        AggregationResults<AnalyzedData> analyzedDataList = mongoTemplate.aggregate(analyzedDataAggregation, AnalyzedData.class);

        dateFilter = match(new Criteria(PROP_WORKSPACE_ID).is(workspaceId).and(CREATED_DATE).gte(localDateTime));
        MatchOperation analyzedDataMatch = match(new Criteria(ANALYZED_DATA_KEY).in(analyzedDataList.getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList())));
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, CREATED_DATE);

        ProjectionOperation projectStage = Aggregation
                .project(PROP_WORKSPACE_ID, CREATED_DATE, ANALYSIS_TIME, INTEGRATION_POINT_KEY)
                .and(CREATED_DATE).dateAsFormattedString(DATE_FORMATTER).as(DATE_HOUR);

        ProjectionOperation finalProjections = Aggregation.project(COUNT, TOTAL_ANALYSIS_TIME)
                .and(ID_DATE_HOUR).as(DATE_TIME)
                .and(ID_INTEGRATION_POINT_KEY).as(INTEGRATION_POINT_KEY);

        GroupOperation groupOperation = group(DATE_HOUR, INTEGRATION_POINT_KEY).count().as(COUNT)
                .sum(ANALYSIS_TIME).as(TOTAL_ANALYSIS_TIME);

        TypedAggregation<StatisticLog> aggregation
                = new TypedAggregation<>(StatisticLog.class, dateFilter, sort, analyzedDataMatch, projectStage, groupOperation, finalProjections);

        AggregationResults<AggregatedAnalysisCount> result = mongoTemplate.aggregate(aggregation, AggregatedAnalysisCount.class);

        return result.getMappedResults();
    }

    public Page<StatisticLog> getSegmentStatistic(String workspaceId, LocalDateTime start, LocalDateTime end, String criteria, String value, Pageable pageable) {
        var startDate = start.toInstant(ZoneOffset.UTC);
        var endDate = end.toInstant(ZoneOffset.UTC);
        var criteriaField = convertToNodeValues(criteria);
        var values = resolvePossibleValueType(value);

        var analyzedDataQuery = Arrays.asList(
                match(Criteria.where(PROP_WORKSPACE_ID).is(workspaceId).and(LAST_MODIFIED_DATE).gte(startDate).lte(endDate).and(criteriaField).in(values)),
                project(HASH));

        var analyzedDataHashes = this.aggregate(AnalyzedData.class, analyzedDataQuery).getMappedResults().stream().map(AnalyzedData::getHash).collect(Collectors.toList());

        var statisticMatch = match(Criteria.where(PROP_WORKSPACE_ID).is(workspaceId)
                .and(CREATED_DATE).gte(startDate).lt(endDate)
                .and(ANALYZED_DATA_KEY).in(analyzedDataHashes)
        );

        var statisticCount = count(StatisticLog.class, statisticMatch);

        var statisticAggregations = new ArrayList<>(Arrays.asList(statisticMatch,
                new SkipOperation((long) pageable.getPageNumber() * pageable.getPageSize()),
                limit(pageable.getPageSize())));

        addStatisticOrder(pageable.getSort(), statisticAggregations);

        return new PageImpl<>(this.aggregate(StatisticLog.class, statisticAggregations).getMappedResults(), pageable, statisticCount);
    }

    private String convertToNodeValues(String criteria) {
        return NODE_VALUES + criteria.replace(".", "#");
    }

    private void addStatisticOrder(Sort sort, List<AggregationOperation> aggregationOperation){
        if (sort.isUnsorted()) {
            aggregationOperation.add(Aggregation.sort(Sort.Direction.DESC, CREATED_DATE));
        } else {
            boolean segmentsCount = sort.stream().anyMatch(it -> SEGMENTS_COUNT.equals(it.getProperty()));
            if (segmentsCount){
                ArrayOperators.Size segmentsCountField = ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull(SEGMENTS_COUNT).then(Collections.emptyList()));
                aggregationOperation.add(Aggregation.addFields().addField(SEGMENTS_COUNT).withValueOf(segmentsCountField).build());
            }
            sort.forEach(it -> aggregationOperation.add(Aggregation.sort(it.getDirection(), it.getProperty())));
        }
    }

    private <T> long count(Class<T> clazz, AggregationOperation... operations) {
        var countOperations = new ArrayList<>(Arrays.asList(operations));
        countOperations.add(Aggregation.count().as(COUNT));

        var countAggregation = newAggregation(clazz, countOperations);

        return Optional.of(mongoTemplate.aggregate(countAggregation, TotalCount.class))
                .map(AggregationResults::getUniqueMappedResult)
                .map(TotalCount::getCount)
                .orElse(0L);
    }

    private <T> AggregationResults<T> aggregate(Class<T> clazz, List<AggregationOperation> operations) {
        return mongoTemplate.aggregate(newAggregation(clazz, operations), clazz);
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
