package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.workpsace.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

}
