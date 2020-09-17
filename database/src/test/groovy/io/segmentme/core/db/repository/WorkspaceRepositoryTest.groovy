package io.segmentme.core.db.repository

import io.segmentme.core.db.helper.UserHelper
import io.segmentme.core.db.helper.WorkspaceHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static io.segmentme.core.db.helper.UserProfileHelper.createProfile

@SpringBootTest
class WorkspaceRepositoryTest extends Specification {

    @Autowired
    private WorkspaceHelper workspaceHelper;

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private WorkspaceRepository repository

    def 'save workspace'() {

        given:
        def user = userHelper.createAndSaveUser();
        def workspace = workspaceHelper.createAndSaveWorkspace()
        workspace.setUserProfiles(Arrays.asList(createProfile(user, workspace)))

        when:
        def save = repository.save(workspace)

        then:
        def found = repository.findById(save.getId())
        found.get() == save
    }
}
