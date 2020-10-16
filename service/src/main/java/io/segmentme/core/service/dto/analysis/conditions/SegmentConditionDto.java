package io.segmentme.core.service.dto.analysis.conditions;

import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionDto extends AbstractConditionDto {

    @Valid
    @NotEmpty
    private SegmentDto segment;
}
