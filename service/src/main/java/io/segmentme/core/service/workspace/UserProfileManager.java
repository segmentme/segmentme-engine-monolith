package io.segmentme.core.service.workspace;

import io.segmentme.core.db.service.workspace.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileManager {
    private final UserProfileService userProfileService;

}
