package io.segmentme.core.api.dto;

import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceDetails {
    private String id;

    private String name;

    private WorkspaceConfiguration configuration;

    private List<IntegrationPoint> integrationPoints;

}
