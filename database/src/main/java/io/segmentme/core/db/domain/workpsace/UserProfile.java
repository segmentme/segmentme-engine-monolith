package io.segmentme.core.db.domain.workpsace;

import io.segmentme.core.db.config.mongo.BackReferenceId;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "userProfile")
public class UserProfile extends DbObject {
    @BackReferenceId("userProfiles")
    @Indexed
    private String workspaceId;

    @Indexed
    private String userId;

    private String workspaceName;

    private Role role;

    private boolean isDefault;
}
