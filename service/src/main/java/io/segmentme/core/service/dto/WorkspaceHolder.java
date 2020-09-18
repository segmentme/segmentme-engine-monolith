package io.segmentme.core.service.dto;

import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceHolder {
    private String id;

    private String name;

    private WorkspaceConfiguration workspaceConfiguration;

    private List<IntegrationPoint> integrationPoints;
}
