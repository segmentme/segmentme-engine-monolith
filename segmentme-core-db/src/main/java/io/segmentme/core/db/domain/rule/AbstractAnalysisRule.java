package io.segmentme.core.db.domain.rule;

import io.segmentme.core.db.domain.condition.AbstractCondition;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "analysisRule")
public abstract class AbstractAnalysisRule<T> {

    @Id
    private String id;

    private AggregationType aggregation;

    private List<String> flags;

    private List<AbstractCondition<?>> conditions;

    public T value;
}
