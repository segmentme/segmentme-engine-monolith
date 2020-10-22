package io.segmentme.core.db.service.statistic;

import io.segmentme.core.db.domain.statistic.StatisticLog;
import io.segmentme.core.db.repository.StatisticRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import org.springframework.stereotype.Service;

@Service
public class StatisticService extends AbstractDatabaseService<StatisticLog, StatisticRepository> {
}
