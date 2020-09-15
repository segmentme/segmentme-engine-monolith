package io.segmentme.core.db.repository

import io.segmentme.core.db.domain.context.ContextSchema
import io.segmentme.core.db.domain.context.SchemaNode
import io.segmentme.core.db.domain.context.SchemaNodeType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class AnalysisContextRepositoryTest extends Specification {

    @Autowired
    private AnalysisContextSchemaRepository analysisContextRepository

    def 'saveContext'() {
        given:
        def analysisContext = new ContextSchema()
                .setRootNode(new SchemaNode().setName("node").setRoot(true).setSubType(SchemaNodeType.STRING))
        Map<String, SchemaNodeType> schemaNodeMap = new HashMap<>();
        schemaNodeMap.put("node", SchemaNodeType.STRING)
        analysisContext.setInlinePath(schemaNodeMap)

        when:
        def save = analysisContextRepository.save(analysisContext)

        then:
        def found = analysisContextRepository.findById(save.getId())
        found.get().getId() == save.getId()
        found.get().getRootNode() == analysisContext.getRootNode()
        found.get().getInlinePath() == analysisContext.getInlinePath()
    }

}
