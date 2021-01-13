package io.segmentme.core.db.domain.workpsace;

import io.segmentme.db.config.domain.DbObject;
import io.segmentme.db.config.mongo.CascadeSave;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "workspace")
public class Workspace extends DbObject {
    private String name;

    private List<IntegrationPoint> integrationPoints;

    private WorkspaceConfiguration configuration;

    @DBRef
    @CascadeSave
    private List<UserProfile> userProfiles;

    private boolean isDefault;

}
