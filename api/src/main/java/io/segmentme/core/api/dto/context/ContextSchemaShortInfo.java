package io.segmentme.core.api.dto.context;

import lombok.Data;

@Data
public class ContextSchemaShortInfo {
    private String integrationPointKey;

    private String name;

    private String id;

    private String hash;
}
