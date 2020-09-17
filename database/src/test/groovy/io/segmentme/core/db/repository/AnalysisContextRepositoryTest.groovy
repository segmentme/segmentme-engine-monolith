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
    private ContextSchemaRepository analysisContextRepository

    def 'saveContext'() {
        given:
        def analysisContext = new ContextSchema()
                .setRootNode(new SchemaNode().setName("node").setSubType(SchemaNodeType.STRING))
        Map<String, ContextSchema.InlineType> schemaNodeMap = new HashMap<>();
        schemaNodeMap.put("node", ContextSchema.InlineType.of(SchemaNodeType.STRING, null))
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
