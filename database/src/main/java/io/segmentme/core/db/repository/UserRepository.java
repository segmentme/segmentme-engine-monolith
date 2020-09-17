package io.segmentme.core.db.repository;

import io.segmentme.core.db.domain.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
