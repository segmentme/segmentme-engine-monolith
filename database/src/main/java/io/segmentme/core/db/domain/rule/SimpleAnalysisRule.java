package io.segmentme.core.db.domain.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SimpleAnalysisRule<T> extends AbstractAnalysisRule<T> {

    private List<String> flags;

}
