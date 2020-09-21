package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.workpsace.Workspace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WorkspaceRepository extends MongoRepository<Workspace, String> {

    Optional<Workspace> findByIntegrationPointsKey(String integrationPointKey);

}
