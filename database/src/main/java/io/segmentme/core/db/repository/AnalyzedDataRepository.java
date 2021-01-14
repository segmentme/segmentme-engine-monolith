package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.statistic.AnalyzedData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyzedDataRepository extends MongoRepository<AnalyzedData, String> {

    AnalyzedData findByHash(String hash);


}
