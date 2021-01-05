package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.repository.AnalyzedDataRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyzedDataService extends AbstractDatabaseService<AnalyzedData, AnalyzedDataRepository> {

    public AnalyzedData findByHash(String hash) {
        return repository.findByHash(hash);
    }

    public AnalyzedData insertIfNotExists(AnalyzedData analyzedData) {
        AnalyzedData existed = repository.findByHash(analyzedData.getHash());
        repository.save(existed != null ? existed : analyzedData);
        return analyzedData;
    }
}
