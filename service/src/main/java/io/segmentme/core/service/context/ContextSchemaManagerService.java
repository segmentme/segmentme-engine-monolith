package io.segmentme.core.service.context;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaManagerService {

    private final ContextSchemaValidationService validationService;

//    public ContextSchema save(ContextSchema contextSchema) throws AnalysisContextValidationException {
//        List<ContextSchemaValidationService.SchemaValidationEntry> validationResult = validationService.validate(contextSchema);
//
//        if (validationResult.stream().anyMatch(it -> it.getSeverity() == ContextSchemaValidationService.ContextValidationEntrySeverity.CRITICAL)) {
//            throw new AnalysisContextValidationException().setSchemaValidationResult(validationResult);
//        }
//        return repository.save(contextSchema);
//    }
//
//    @Override
//    public Optional<ContextSchema> findById(String id) {
//        return repository.findById(id);
//    }
}
