package io.segmentme.core.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.segmentme.core.service.context.ContextSchemaValidationService.ContextValidationEntrySeverity.CRITICAL;
import static io.segmentme.core.service.context.ContextSchemaValidationService.ContextValidationEntrySeverity.MID;

public interface ContextSchemaValidationService {

    String CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT = "context.should.contain.at.least.one.element";
    String ROOT_NODE_SHOULD_BE_OBJECT = "root.should.be.an.object";
    String ROOT_NODE_SHOULDNT_HAVE_SUBTUPES = "root.shouldnt.have.subtypes";
    String NODE_TYPE_NOT_DEFINED = "node.type.not.defined";
    String NODE_SUBTYPE_NOT_DEFINED = "node.subtype.not.defined";
    String NODE_NAME_NOT_DEFINED = "node.name.not.defined";
    String NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED = "node.subtype.should.not.be.defined";


    List<SchemaValidationEntry> validate(ContextSchema schema);

    @Data
    final class SchemaValidationEntry {
        private String code;
        private String path;
        private ContextSchemaValidationServiceImpl.ContextValidationEntrySeverity severity;


    }

    enum ContextValidationEntrySeverity {
        LOW, MID, CRITICAL
    }

    Map<String, ContextValidationEntrySeverity> ERRORS_SEVERITY = new HashMap<>() {
        {
            put(CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT, CRITICAL);
            put(ROOT_NODE_SHOULD_BE_OBJECT, CRITICAL);
            put(ROOT_NODE_SHOULDNT_HAVE_SUBTUPES, CRITICAL);
            put(NODE_TYPE_NOT_DEFINED, CRITICAL);
            put(NODE_SUBTYPE_NOT_DEFINED, CRITICAL);
            put(NODE_NAME_NOT_DEFINED, CRITICAL);
            put(NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED, MID);
        }
    };

}
