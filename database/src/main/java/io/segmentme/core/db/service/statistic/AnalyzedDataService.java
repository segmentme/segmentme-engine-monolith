package io.segmentme.core.db.service.statistic;

import com.mongodb.client.result.UpdateResult;
import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.repository.AnalyzedDataRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnalyzedDataService extends AbstractDatabaseService<AnalyzedData, AnalyzedDataRepository> {

    private final MongoTemplate mongoTemplate;

    public AnalyzedData findByHash(String hash) {
        return repository.findByHash(hash);
    }

    public AnalyzedData insertIfNotExists(AnalyzedData analyzedData) {
        Document doc = new Document(); // org.bson.Document
        mongoTemplate.getConverter().write(analyzedData, doc);
        Update update = Update.fromDocument(doc);
        UpdateResult hash = mongoTemplate.upsert(new Query(Criteria.where("hash").is(analyzedData.getHash())), update, AnalyzedData.class);
        analyzedData.setId(Objects.requireNonNull(hash.getUpsertedId()).asObjectId().toString());
        return analyzedData;
    }
}
