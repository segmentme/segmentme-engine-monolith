package io.segmentme.core.db.domain.context;

import lombok.Getter;

@Getter
public enum SchemaNodeType {
    OBJECT, ARRAY, STRING, DATE, NUMBER, BOOLEAN, UNDEFINED;
}
