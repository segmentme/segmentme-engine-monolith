package io.segmentme.core.db.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.repository.ContextSchemaRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaServiceImpl extends AbstractDatabaseService<ContextSchema, ContextSchemaRepository> {


}
