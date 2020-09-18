package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.workpsace.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    List<UserProfile> findAllByUserId(String id);
}
