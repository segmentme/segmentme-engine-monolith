package io.segmentme.core.db.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.exception.AnalysisContextValidationException;
import io.segmentme.core.db.repository.AnalysisContextSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaServiceImpl implements ContextSchemaService {

    private final AnalysisContextSchemaRepository repository;
    private final ContextSchemaValidationService validationService;

    @Override
    public ContextSchema save(ContextSchema contextSchema) throws AnalysisContextValidationException {
        List<ContextSchemaValidationService.SchemaValidationEntry> validationResult = validationService.validate(contextSchema);

        if (validationResult.stream().anyMatch(it -> it.getSeverity() == ContextSchemaValidationService.ContextValidationEntrySeverity.CRITICAL)) {
            throw new AnalysisContextValidationException().setSchemaValidationResult(validationResult);
        }
        return repository.save(contextSchema);
    }

    @Override
    public Optional<ContextSchema> findById(String id) {
        return repository.findById(id);
    }
}
