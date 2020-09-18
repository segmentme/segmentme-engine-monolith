package io.segmentme.core.service.user

import io.segmentme.core.db.domain.workpsace.Role
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration
import io.segmentme.core.db.service.user.UserService
import io.segmentme.core.db.service.workspace.UserProfileService
import io.segmentme.core.db.service.workspace.WorkspaceService
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.workspace.WorkspaceManager
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.service.helper.UserHolderHelper.createUser

class UserManagerTest extends BaseTestWithContext {
    @Autowired
    private UserManager userManager;

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    def "User creation should lead to creating minimal viable state"() {
        given:
        def user = createUser()
        when:
        def createdUser = userManager.createUser(user)
        then:
        def userToValidate = userService.findById(createdUser.getId()).get()
        assert userToValidate.getId() == createdUser.getId()

        def profile = userProfileService.getUserProfiles(userToValidate.getId()).get(0)

        assert profile.getRole() == Role.OWNER

        def workspace = workspaceService.findById(profile.getWorkspaceId()).get()
        assert workspace.name == "DEFAULT"
        assert workspace.configuration == new WorkspaceConfiguration().setKnownDateFormats(WorkspaceManager.DEFAULT_DATE_PATTERNS)
        assert workspace.integrationPoints.size() == 1
    }
}
