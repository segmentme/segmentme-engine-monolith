package io.segmentme.core.service.dto.analysis.segment;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.dto.analysis.conditions.AbstractConditionDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SegmentDto {

    private String id;

    @NotNull
    private Segment.AggregationType aggregation;

    @NotEmpty
    private String name;

    @NotEmpty
    private List<AbstractConditionDto> conditions;

    private boolean matchResult;

    private boolean embedded;

}
