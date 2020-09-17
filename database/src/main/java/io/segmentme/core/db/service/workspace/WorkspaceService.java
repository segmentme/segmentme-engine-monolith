package io.segmentme.core.db.service.workspace;

import io.segmentme.core.db.domain.workpsace.Workspace;
import io.segmentme.core.db.repository.WorkspaceRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class WorkspaceService extends AbstractDatabaseService<Workspace, WorkspaceRepository> {
}
