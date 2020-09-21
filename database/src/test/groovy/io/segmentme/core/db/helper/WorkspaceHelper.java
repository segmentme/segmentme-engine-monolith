package io.segmentme.core.db.helper;

import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.db.repository.WorkspaceRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
@Data
class WorkspaceHelper {

    private final WorkspaceRepository workspaceRepository;

    public static Workspace createWorkspace() {
        return new Workspace()
                .setName("default")
                .setConfiguration(new WorkspaceConfiguration().setKnownDateFormats(Arrays.asList("yyyy-mm-dd")))
                .setIntegrationPoints(Arrays.asList(new IntegrationPoint().setKey(UUID.randomUUID().toString())));
    }

    public Workspace createAndSaveWorkspace() {
        return workspaceRepository.save(createWorkspace());
    }


}
