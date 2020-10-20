package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.statistic.StatisticLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatisticRepository extends MongoRepository<StatisticLog, String> {
}
