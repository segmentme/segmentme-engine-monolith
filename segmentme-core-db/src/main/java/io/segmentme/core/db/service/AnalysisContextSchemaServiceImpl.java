package io.segmentme.core.db.service;

import io.segmentme.core.db.domain.context.AnalysisContextSchema;
import io.segmentme.core.db.exception.AnalysisContextValidationException;
import io.segmentme.core.db.repository.AnalysisContextSchemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisContextSchemaServiceImpl implements AnalysisContextSchemaService {

    private AnalysisContextSchemaRepository repository;

    @Override
    public AnalysisContextSchema save(AnalysisContextSchema analysisContextSchema) throws AnalysisContextValidationException {

        return repository.save(analysisContextSchema);
    }

    @Override
    public Optional<AnalysisContextSchema> findById(String id) {
        return repository.findById(id);
    }
}
