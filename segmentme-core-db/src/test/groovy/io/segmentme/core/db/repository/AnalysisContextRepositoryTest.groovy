package io.segmentme.core.db.repository

import io.segmentme.core.db.domain.context.AnalysisContext
import io.segmentme.core.db.domain.context.ScehamNodeType
import io.segmentme.core.db.domain.context.SchemaNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class AnalysisContextRepositoryTest extends Specification {

    @Autowired
    private AnalysisContextRepository analysisContextRepository

    def 'saveContext'() {
        given:
        def analysisContext = new AnalysisContext()
                .setRootNode(new SchemaNode().setName("node").setRoot(true).setSubType(ScehamNodeType.STRING))
        when:
        def save = analysisContextRepository.save(analysisContext)
        then:
        def found = analysisContextRepository.findById(save.getId())
        found.get().getId() == save.getId()
    }

}
