package io.segmentme.core.db.service.statistic


import io.segmentme.core.db.domain.statistic.StatisticLog
import io.segmentme.core.db.repository.StatisticRepository
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
class StatisticServiceTest extends Specification {


    @Autowired
    private StatisticRepository repository
    @Autowired
    private StatisticService statisticService;

    def 'get analysis count result '() {
        given:
        def datesToSave = [] as List<StatisticLog>
        String workspaceId = UUID.randomUUID().toString()
        for (int i = 0; i < 200; i++) {
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setAnalysisTime(RandomUtils.nextLong()))
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setAnalysisTime(RandomUtils.nextLong()))

        }
        repository.saveAll(datesToSave)
        for (int i = 0; i < 200; i++) {
            datesToSave[i].setCreatedDate(Instant.now().minus(i,ChronoUnit.DAYS))
            datesToSave[i+1].setCreatedDate(Instant.now().minus(i,ChronoUnit.HOURS))

        }
        when:
        repository.saveAll(datesToSave)
        then:
        def found = statisticService.getAnalysisCount(workspaceId, 1);
       !found.isEmpty()
    }


    def 'test find by integration point key'() {
        when:
        def workspaceToFind = workspaceHelper.createAndSaveWorkspace()
        workspaceHelper.createAndSaveWorkspace()
        then:
        def found = repository.findByIntegrationPointsKey(workspaceToFind.integrationPoints[0].key)
        found.get() == workspaceToFind
    }

}
