package io.segmentme.core.service.context


import io.segmentme.core.db.domain.context.SchemaNode
import io.segmentme.core.db.domain.context.SchemaNodeType
import io.segmentme.core.db.domain.workpsace.Workspace
import io.segmentme.core.db.service.workspace.WorkspaceService
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.exception.AbstractManagerException
import io.segmentme.core.service.exception.ContextSchemaManagerException
import io.segmentme.core.service.exception.ContextSchemaValidationException
import io.segmentme.core.service.exception.error.ContextMangerErrors
import io.segmentme.core.service.exception.error.ContextValidationErrors
import io.segmentme.core.service.helper.UserHolderHelper
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

import static io.segmentme.core.service.context.ContextSchemaValidationService.SchemaValidationEntry

class ContextSchemaManagerTest extends BaseTestWithContext {

    public static final String UNKNOWN_INTEGRATION_POINT = "unknown-integration-point"
    public static final SchemaNode validSchema = new SchemaNode().setName("ROOT").setType(SchemaNodeType.OBJECT).setSubNodes(Arrays.asList(new SchemaNode().setName("name").setType(SchemaNodeType.NUMBER)))

    @Autowired
    private UserHolderHelper userHelper

    @Autowired
    private WorkspaceService workspaceService

    @Autowired
    private ContextSchemaManager contextSchemaManager

    @Shared
    List<Workspace> workspaces = new ArrayList<>()

    def "Create context #rootNode for integration key #integrationKey should #result"() {
        setup:
        def user = userHelper.createUserAndState();
        workspaces = workspaceService.findAllUserWorkspaces(user.getId())
        expect:
        try {
            def create = contextSchemaManager.create(integrationKey ?: workspaces[0].integrationPoints[0].key, rootNode, null, null)
            assert create != null && result == true
        } catch (AbstractManagerException ex) {
            assert ex == result
        }
        where:
        rootNode                                                        | integrationKey            || result
        validSchema                                                     | null                      || true
        validSchema                                                     | UNKNOWN_INTEGRATION_POINT || new ContextSchemaManagerException().setCode(ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND)
        new SchemaNode().setName("root").setType(SchemaNodeType.OBJECT) | null                      || new ContextSchemaValidationException().setSchemaValidationResult([new SchemaValidationEntry().setPath("root").setCode(ContextValidationErrors.CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT)])


    }

    def "UpdateContextSchema"() {
    }

}
