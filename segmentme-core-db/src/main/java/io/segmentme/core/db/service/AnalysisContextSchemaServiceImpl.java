package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
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
public class AnalysisContextSchemaServiceImpl implements AnalysisContextSchemaService {

    private final AnalysisContextSchemaRepository repository;
    private final AnalysisContextSchemaValidationService validationService;

    @Override
    public AnalysisContextSchema save(AnalysisContextSchema analysisContextSchema) throws AnalysisContextValidationException {
        List<AnalysisContextSchemaValidationService.SchemaValidationEntry> validationResult = validationService.validate(analysisContextSchema);

        if (validationResult.stream().anyMatch(it -> it.getSeverity() == AnalysisContextSchemaValidationService.ContextValidationEntrySeverity.CRITICAL)) {
            throw new AnalysisContextValidationException().setSchemaValidationResult(validationResult);
        }
        return repository.save(analysisContextSchema);
    }

    @Override
    public Optional<AnalysisContextSchema> findById(String id) {
        return repository.findById(id);
    }
}
