package io.segmentme.core.db.domain.workpsace;

import io.segmentme.core.db.config.mongo.BackReferenceId;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "user_profile")
public class UserProfile extends DbObject {
    @BackReferenceId("userProfiles")
    private String workspaceId;

    private String userId;

    private Role role;
}
