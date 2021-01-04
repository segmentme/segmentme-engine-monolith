package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.service.dto.context.ContextSchemaHolder;

public class ContextSchemaConverter {

    private ContextSchemaConverter() {
    }

    public static ContextSchemaHolder toHolder(ContextSchema contextSchema) {
        return new ContextSchemaHolder().setId(contextSchema.getId())
            .setName(contextSchema.getName())
            .setInlinePath(contextSchema.getInlinePath())
            .setRootNode(contextSchema.getRootNode())
            .setRawPayload(contextSchema.getRawPayload())
            .setNodeValues(contextSchema.getNodeValues())
            .setHash(contextSchema.getHash())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey());
    }

}
