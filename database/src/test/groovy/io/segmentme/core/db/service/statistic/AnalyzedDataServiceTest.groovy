package io.segmentme.core.db.service.statistic

import io.segmentme.core.db.domain.statistic.AnalyzedData
import io.segmentme.core.db.repository.AnalyzedDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class AnalyzedDataServiceTest extends Specification {


    @Autowired
    private AnalyzedDataService analyzedDataService;


    @Autowired
    private AnalyzedDataRepository analyzedDataRepository

    def 'get latest client analyzed data'() {
        given:
        analyzedDataRepository.save(new AnalyzedData().setIntegrationPointKey("ip1").setClientId("c1").setAnalyzedSegments(["1", "2", "3"]));
        analyzedDataRepository.save(new AnalyzedData().setIntegrationPointKey("ip2").setClientId("c1").setAnalyzedSegments(["1", "2", "3"]));
        analyzedDataRepository.save(new AnalyzedData().setIntegrationPointKey("ip1").setClientId("c2").setAnalyzedSegments(["3", "4", "5"]));
        analyzedDataRepository.save(new AnalyzedData().setIntegrationPointKey("ip1").setClientId("c2").setAnalyzedSegments(["2", "6", "7"]));

        when:
        def found = analyzedDataService.findLatestClientsAnalyzedData("ip1","2",["c1","c2"])

        then:
        found.size()==2
    }


}
