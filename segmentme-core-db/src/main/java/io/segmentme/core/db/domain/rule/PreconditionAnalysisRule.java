package io.segmentme.core.db.domain.rule;

import io.segmentme.core.db.config.mongo.CascadeSave;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "preconditionAnalysisRule")
public class PreconditionAnalysisRule extends AbstractAnalysisRule<Boolean> {

    @DBRef
    @CascadeSave
    private List<? extends SimpleAnalysisRule<?>> analysisRules;
}
