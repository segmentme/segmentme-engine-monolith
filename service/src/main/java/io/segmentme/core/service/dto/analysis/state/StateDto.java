package io.segmentme.core.service.dto.analysis.state;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class StateDto {

    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String integrationPointKey;

    @NotNull
    private JsonNode value;

    private JsonNode defaultValue;

    @NotNull
    private SegmentShortInfo segment;

}
