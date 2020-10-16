package io.segmentme.core.db.domain.segment;

import lombok.Data;

@Data
public class State {

    private String name;

    private Object value;

    private Segment rule;
}
