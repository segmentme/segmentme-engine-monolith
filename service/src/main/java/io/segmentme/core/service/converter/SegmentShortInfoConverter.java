package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.segment.Segment;
import io.segmentme.core.service.dto.analysis.segment.SegmentDto;
import io.segmentme.core.service.dto.analysis.segment.SegmentShortInfo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SegmentShortInfoConverter {

    public SegmentShortInfo of(Segment source) {
        return new SegmentShortInfo()
                .setId(source.getId())
                .setName(source.getName());
    }

    public SegmentShortInfo of(SegmentDto source) {
        return new SegmentShortInfo()
                .setId(source.getId())
                .setName(source.getName());
    }
}
