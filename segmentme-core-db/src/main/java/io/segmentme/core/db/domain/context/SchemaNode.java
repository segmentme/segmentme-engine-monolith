package io.segmentme.core.db.domain.context;

import lombok.Data;

import java.util.List;

@Data
public class SchemaNode {
    private String name;

    private boolean root;

    private ScehamNodeType type;

    private ScehamNodeType subType;

    private List<SchemaNode> subNodes;
}
