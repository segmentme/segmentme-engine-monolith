package io.segmentme.core.db.domain.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


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
        @Type(name = "GROUP", value = GroupCondition.class),
        @Type(name = "CONTAINS_ALL", value = ArrayCondition.class),
        @Type(name = "CONTAINS_ANY", value = ArrayCondition.class),
        @Type(name = "CONTAINS_ONLY", value = ArrayCondition.class)
})
public abstract class AbstractCondition<T>  extends DbObject {

    private String name;

    private String criteria;

    private String description;

    private ConditionType type;

    private boolean matchResult = true;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Indexed
    private String contextId;

    public enum ConditionType {
        IN, RANGE, GT, GTE, LT, LTE, GROUP, CONTAINS_ALL, CONTAINS_ANY, CONTAINS_ONLY
    }
}
