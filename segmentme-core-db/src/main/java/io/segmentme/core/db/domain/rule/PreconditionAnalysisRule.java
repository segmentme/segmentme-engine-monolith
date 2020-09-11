package io.segmentme.core.db.domain.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PreconditionAnalysisRule extends AbstractAnalysisRule<Boolean> {

    @DBRef
    private List<? extends AbstractAnalysisRule<?>> analysisRules;
}
