package io.segmentme.core.db.domain.context;

import lombok.Data;

import java.util.List;

@Data
public class SchemaNode {
    private String name;

    private SchemaNodeType type;

    private SchemaNodeType subType;

    private List<SchemaNode> subNodes;
}
