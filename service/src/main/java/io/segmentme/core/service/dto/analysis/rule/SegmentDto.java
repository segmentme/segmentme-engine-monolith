package io.segmentme.core.service.dto.analysis.rule;

import io.segmentme.core.db.config.mongo.CascadeSave;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.rule.Segment;
import io.segmentme.core.service.dto.analysis.component.AbstractConditionDto;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
public class SegmentDto {

    private String id;

    private Segment.AggregationType aggregation;

    private String name;

    private List<AbstractConditionDto> conditions;

    private boolean matchResult;

    private boolean embedded;

}
