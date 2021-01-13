package io.segmentme.core.db.domain.segment;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.db.config.domain.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@Data
@Document(collection = "segment")
@EqualsAndHashCode(callSuper = true)
public class Segment extends DbObject {

    private AggregationType aggregation;

    @EqualsAndHashCode.Exclude
    private String name;

    private boolean active;

    @EqualsAndHashCode.Exclude
    private String description;

    @Indexed
    private String integrationPointKey;

    private List<AbstractCondition> conditions;

    private boolean matchResult;

    @EqualsAndHashCode.Exclude
    private String hash;

    @Indexed
    private String contextId;

    public void recalculateHash() {
        Optional.ofNullable(conditions).ifPresent(it -> it.forEach(AbstractCondition::recalculateHash));
        this.hash = String.valueOf(this.hashCode());
    }

    public enum AggregationType {
        AND,
        OR
    }
}


