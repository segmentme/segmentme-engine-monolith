package io.segmentme.core.db.domain.condition;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArrayCondition extends SimpleCondition<List<Object>> {
}
