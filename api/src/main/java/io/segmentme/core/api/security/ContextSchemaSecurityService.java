package io.segmentme.core.api.security;

import io.segmentme.core.api.config.SecurityUtils;
import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.service.context.ContextSchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContextSchemaSecurityService {

    private final ContextSchemaService contextSchemaService;

    private final SecurityService securityService;

    public boolean isMangedSchema(String contextSchemaId) {
        return contextSchemaService.findById(contextSchemaId)
                .map(ContextSchema::getIntegrationPointKey)
                .map(it -> securityService.isValidIntegrationPointKey(it, SecurityUtils.currentUserId()))
                .orElse(false);
    }
}

