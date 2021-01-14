package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.repository.AnalyzedDataRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class AnalyzedDataService extends AbstractDatabaseService<AnalyzedData, AnalyzedDataRepository> {
    public static final String DATA = "data";
    private final MongoTemplate mongoTemplate;

    public AnalyzedData findByHash(String hash) {
        return repository.findByHash(hash);
    }

    public void insertIfNotExists(AnalyzedData analyzedData) {
        AnalyzedData existed = repository.findByHash(analyzedData.getHash());
        repository.save(existed != null ? existed : analyzedData);
    }


    public List<AnalyzedData> findLatestClientsAnalyzedData(String integrationPointKey, String segmentId, List<String> clientIds) {
        MatchOperation matchStage = match(
            new Criteria(INTEGRATION_POINT_KEY)
                .is(integrationPointKey)
                .and(ANALYZED_SEGMENTS)
                .in(segmentId)
                .and(CLIENT_ID).in(clientIds)
        );
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, LAST_MODIFIED_DATE);
        GroupOperation groupBySegment = group(CLIENT_ID).first(ROOT).as(DATA);
        ProjectionOperation analyzedDataProjection = Aggregation.project().
            and(DATA + "." + ID).as(ID)
            .and(DATA + "." + CLIENT_ID).as(CLIENT_ID)
            .and(DATA + "." + INTEGRATION_POINT_KEY).as(INTEGRATION_POINT_KEY)
            .and(DATA + "." + PAYLOAD).as(PAYLOAD);

        TypedAggregation<AnalyzedData> aggregation
            = new TypedAggregation<>(AnalyzedData.class, matchStage, sortOperation, groupBySegment, analyzedDataProjection);

        AggregationResults<AnalyzedData> result = mongoTemplate.aggregate(aggregation, AnalyzedData.class);

        return result.getMappedResults();
    }
}
