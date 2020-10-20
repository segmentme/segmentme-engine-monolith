package io.segmentme.core.service.dto.analysis.conditions;

import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionDto extends SimpleConditionDto<SegmentDto> {
}
