package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.DbObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDatabaseService<E extends DbObject, R extends MongoRepository<E, String>> {

    public static final String PROP_WORKSPACE_ID = "workspaceId";
    public static final String PROP_SEGMENT_STATISTICS = "segmentStatistics";
    public static final String CREATED_DATE = "createdDate";
    public static final String SEGMENT_STATISTICS_SEGMENT_ID = "segmentStatistics.segmentId";
    public static final String SEGMENT_ID = "segmentId";
    public static final String SEGMENT_STATISTICS_RESULT = "segmentStatistics.result";
    public static final String SEGMENT_RESULT = "segmentResult";
    public static final String COUNT = "count";
    public static final String ID = "_id";
    public static final String ANALYSIS_TIME = "analysisTime";
    public static final String INTEGRATION_POINT_KEY = "integrationPointKey";
    public static final String CLIENT_ID = "clientId";
    public static final String ANALYZED_SEGMENTS = "analyzedSegments";
    public static final String DATE_FORMATTER = "%Y-%m-%dT%H:00:00";
    public static final String DATE_HOUR = "dateHour";
    public static final String TOTAL_ANALYSIS_TIME = "totalAnalysisTime";
    public static final String ID_DATE_HOUR = "_id.dateHour";
    public static final String DATE_TIME = "dateTime";
    public static final String ID_INTEGRATION_POINT_KEY = "_id.integrationPointKey";
    public static final String NODE_VALUES = "nodeValues.";
    public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String HASH = "hash";
    public static final String ANALYZED_DATA_KEY = "analyzedDataKey";
    public static final String SEGMENTS_COUNT = "segmentsCount";
    public static final String PAYLOAD = "payload";

    @Autowired
    protected R repository;

    public Optional<E> findById(String id) {
        return repository.findById(id);
    }

    public E create(E entity) {
        return repository.save(entity);
    }

    public E update(E update) {
        return repository.save(update);
    }

    public Collection<E> updateAll(Collection<E> update) {
        return repository.saveAll(update);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public void delete(E entity) {
        repository.delete(entity);
    }

    public void deleteAll(Collection<E> entity) {
        repository.deleteAll(entity);
    }

    public Iterable<E> findByIds(List<String> userIds) {
        return repository.findAllById(userIds);
    }
}
