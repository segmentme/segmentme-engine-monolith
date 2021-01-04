package io.segmentme.core.service.dto.analysis.conditions;

import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SegmentConditionDto extends SimpleConditionDto<SegmentDto> {
}
