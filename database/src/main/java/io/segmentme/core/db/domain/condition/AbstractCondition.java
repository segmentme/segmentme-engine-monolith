package io.segmentme.core.db.domain.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("condition")
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes(value = {
        @Type(name = "IN", value = ArrayCondition.class),
        @Type(name = "RANGE", value = RangeCondition.class),
        @Type(name = "GT", value = SingleCondition.class),
        @Type(name = "GTE", value = SingleCondition.class),
        @Type(name = "LT", value = SingleCondition.class),
        @Type(name = "LTE", value = SingleCondition.class),
        @Type(name = "SEGMENT", value = SegmentCondition.class),
        @Type(name = "CONTAINS_ALL", value = ArrayCondition.class),
        @Type(name = "CONTAINS_ANY", value = ArrayCondition.class),
        @Type(name = "CONTAINS_ONLY", value = ArrayCondition.class)
})
public abstract class AbstractCondition extends DbObject {

    private String name;

    private String criteria;

    private String description;

    private ConditionType type;

    private boolean matchResult = true;

    private boolean embedded;

    @Indexed
    private String contextId;

    public enum ConditionType {
        IN, RANGE, GT, GTE, LT, LTE, SEGMENT, CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY
    }
}
