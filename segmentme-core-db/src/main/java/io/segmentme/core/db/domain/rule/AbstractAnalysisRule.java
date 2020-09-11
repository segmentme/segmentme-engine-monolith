package io.segmentme.core.db.domain.rule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "analysisRule")
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "ruleType", visible = true)
@JsonSubTypes(value = {
        @Type(name = "BOOLEAN", value = BooleanAnalysisRule.class),
        @Type(name = "PRECONDITION", value = PreconditionAnalysisRule.class)
})
public abstract class AbstractAnalysisRule<T> extends DbObject {

    private RuleType ruleType;

    private AggregationType aggregation;

    private String preconditionId;

    @DBRef
    private List<AbstractCondition<?>> conditions;

    public T value;

    public enum RuleType {
        BOOLEAN, JSON, PRECONDITION
    }

    public enum AggregationType {
        AND,
        OR
    }
}
