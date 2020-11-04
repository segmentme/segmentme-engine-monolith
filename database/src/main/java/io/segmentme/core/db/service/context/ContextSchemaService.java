package io.segmentme.core.db.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.repository.ContextSchemaRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaService extends AbstractDatabaseService<ContextSchema, ContextSchemaRepository> {

    public Optional<ContextSchema> findByHashAndIntegrationPointKey(String integrationPointKey, String hash) {
        return repository.findByIntegrationPointKeyAndHash(integrationPointKey, hash);
    }

    public Optional<ContextSchema> findByIdAndIntegrationPointKey(String id, String integrationPointKey) {
        return repository.findByIdAndIntegrationPointKey(id, integrationPointKey);
    }

    public List<ContextSchema> findByIntegrationPointKeys(Collection<String> integrationPointKeys, boolean shortForm) {
        return shortForm ? repository.findShortFormByIntegrationPointKeyIn(integrationPointKeys) : repository.findByIntegrationPointKeyIn(integrationPointKeys);
    }

    @Override
    public ContextSchema create(ContextSchema entity) {
        entity.setHash(entity.computeHash());
        return super.create(entity);
    }

    @Override
    public ContextSchema update(ContextSchema entity) {
        entity.setHash(entity.computeHash());
        return super.update(entity);
    }
}
