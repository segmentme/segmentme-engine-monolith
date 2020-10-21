package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.state.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StateRepository extends MongoRepository<State, String> {

    List<State> findByWorkSpaceId(String workspaceId);
}
