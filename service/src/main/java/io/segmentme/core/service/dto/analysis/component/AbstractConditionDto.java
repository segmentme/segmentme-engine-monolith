package io.segmentme.core.service.dto.analysis.component;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;


@Data
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes(value = {
        @Type(name = "RANGE", value = RangeConditionDto.class),
        @Type(name = "GT", value = SingleConditionDto.class),
        @Type(name = "GTE", value = SingleConditionDto.class),
        @Type(name = "LT", value = SingleConditionDto.class),
        @Type(name = "LTE", value = SingleConditionDto.class),
        @Type(name = "SEGMENT", value = SegmentConditionDto.class),
        @Type(name = "IN", value = ArrayConditionDto.class),
        @Type(name = "CONTAINS_ALL", value = ArrayConditionDto.class),
        @Type(name = "CONTAINS_ANY", value = ArrayConditionDto.class),
        @Type(name = "CONTAINS_ONLY", value = ArrayConditionDto.class)
})
public abstract class AbstractConditionDto {

    private String id;

    @NotBlank
    private String name;

    private String description;

    private AbstractCondition.ConditionType type;

    private boolean matchResult = true;

    private boolean embedded;

}


