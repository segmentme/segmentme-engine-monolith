package io.segmentme.core.db.domain.rule;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BooleanAnalysisRule extends SimpleAnalysisRule<Boolean> {
}

