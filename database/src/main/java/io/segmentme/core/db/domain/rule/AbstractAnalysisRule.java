package io.segmentme.core.db.domain.rule;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.segmentme.core.db.config.mongo.BackReferenceId;
import io.segmentme.core.db.config.mongo.CascadeSave;
import io.segmentme.core.db.domain.condition.AbstractCondition;
import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Document(collection = "analysisRule")
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "ruleType", visible = true)
@JsonSubTypes(value = {
        @Type(name = "BOOLEAN", value = BooleanAnalysisRule.class),
        @Type(name = "PRECONDITION", value = PreconditionAnalysisRule.class),
        @Type(name = "JSON", value = JsonAnalysisRule.class)
})
public abstract class AbstractAnalysisRule<T> extends DbObject {

    private RuleType ruleType;

    private AggregationType aggregation;

    @BackReferenceId("analysisRules")
    private String preconditionId;

    @Indexed
    private String integrationPointKey;

    @DBRef
    @CascadeSave
    private List<AbstractCondition<?>> conditions;

    public T value;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    private String contextId;



    public enum RuleType {
        BOOLEAN, JSON, PRECONDITION
    }

    public enum AggregationType {
        AND,
        OR
    }
}
