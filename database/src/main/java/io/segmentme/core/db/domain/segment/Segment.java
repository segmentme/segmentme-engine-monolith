package io.segmentme.core.db.domain.segment;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "segment")
@EqualsAndHashCode(callSuper = true, exclude = "conditions")
public class Segment extends DbObject {

    private AggregationType aggregation;

    private String name;

    @Indexed
    private String integrationPointKey;

    private List<AbstractCondition> conditions;

    private boolean matchResult;

    @Indexed
    private String contextId;

    public enum AggregationType {
        AND,
        OR
    }
}


