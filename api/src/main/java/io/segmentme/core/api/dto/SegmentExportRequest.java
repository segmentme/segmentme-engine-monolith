package io.segmentme.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class SegmentExportRequest {
    private String workspaceId;

    private List<String> segmentIds;
}
