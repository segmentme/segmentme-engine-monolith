package io.segmentme.core.db.domain.condition;

import io.segmentme.core.db.config.mongo.CascadeSave;
import io.segmentme.core.db.domain.segment.Segment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentCondition extends AbstractCondition {

    @DBRef
    @CascadeSave
    private Segment segment;

}
