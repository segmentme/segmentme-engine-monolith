package io.segmentme.core.db.domain.condition;

import io.segmentme.core.db.domain.rule.Segment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentCondition extends AbstractCondition {

    @DBRef
    private Segment segment;

}
