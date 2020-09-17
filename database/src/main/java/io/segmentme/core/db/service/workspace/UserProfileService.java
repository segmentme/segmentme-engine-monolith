package io.segmentme.core.db.service.workspace;

import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.db.repository.UserProfileRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@EqualsAndHashCode(callSuper = true)
@Service
@Data
@RequiredArgsConstructor
public class UserProfileService extends AbstractDatabaseService<UserProfile, UserProfileRepository> {
}
