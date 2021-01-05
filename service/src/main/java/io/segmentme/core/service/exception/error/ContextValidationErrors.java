package io.segmentme.core.service.exception.error;

import java.util.Map;

import static io.segmentme.core.service.context.SeverityLevel.CRITICAL;
import static io.segmentme.core.service.context.SeverityLevel.MID;

public class ContextValidationErrors implements Errors {

    public static final String NODE_OBJECT_SHOULD_HAVE_CHILDREN = "context.validation.object.node.should.have.children";
    public static final String CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT = "context.validation.context.should.contain.at.least.one.element";
    public static final String ROOT_NODE_SHOULD_BE_OBJECT = "context.validation.root.should.be.an.object";
    public static final String ROOT_NODE_SHOULDNT_HAVE_SUBTUPES = "context.validation.root.shouldnt.have.subtypes";
    public static final String NODE_TYPE_NOT_DEFINED = "context.validation.node.type.not.defined";
    public static final String NODE_SUBTYPE_NOT_DEFINED = "context.validation.node.subtype.not.defined";
    public static final String NODE_NAME_NOT_DEFINED = "context.validation.node.name.not.defined";
    public static final String NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED = "context.validation.node.subtype.should.not.be.defined";

    static {
        ERRORS_SEVERITY.putAll(Map.of(
            CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT, CRITICAL,
            ROOT_NODE_SHOULD_BE_OBJECT, CRITICAL,
            ROOT_NODE_SHOULDNT_HAVE_SUBTUPES, CRITICAL,
            NODE_TYPE_NOT_DEFINED, CRITICAL,
            NODE_SUBTYPE_NOT_DEFINED, CRITICAL,
            NODE_NAME_NOT_DEFINED, CRITICAL,
            NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED, MID,
            NODE_OBJECT_SHOULD_HAVE_CHILDREN, CRITICAL
            ));
    }
}
