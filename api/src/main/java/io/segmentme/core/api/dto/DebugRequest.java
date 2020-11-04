package io.segmentme.core.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DebugRequest {

    @NotNull
    private JsonNode payload;

    @NotNull
    private SegmentDto segment;
}

