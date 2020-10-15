package io.segmentme.core.db.domain.rule;

import lombok.Data;

@Data
public class State {

    private String name;

    private Object value;

    private Segment rule;
}
