package io.segmentme.core.db.service.user;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.repository.UserRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class UserService extends AbstractDatabaseService<User, UserRepository> {

}
