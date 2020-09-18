package io.segmentme.core.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.service.exception.error.Errors;
import lombok.Data;

import java.util.List;

public interface ContextSchemaValidationService {


    List<SchemaValidationEntry> validate(ContextSchema schema);

    @Data
    final class SchemaValidationEntry {
        private String code;
        private String path;
        private SeverityLevel severity;

        public SchemaValidationEntry setCode(String code) {
            this.code = code;
            this.severity = Errors.getSeverity(code);
            return this;
        }
    }

}
