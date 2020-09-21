package io.segmentme.core.service.context;

import io.segmentme.core.db.domain.context.ContextSchema;
import io.segmentme.core.db.domain.context.SchemaNode;
import io.segmentme.core.db.domain.context.SchemaNodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.segmentme.core.service.exception.error.ContextValidationErrors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextSchemaValidationServiceImpl implements ContextSchemaValidationService {


    public static final String ROOT = "root";

    @Override
    public List<SchemaValidationEntry> validate(ContextSchema contextSchema) {

        List<SchemaValidationEntry> entries = new ArrayList<>();

        SchemaNode rootNode = contextSchema.getRootNode();
        if (rootNode == null || CollectionUtils.isEmpty(rootNode.getSubNodes())) {
            entries.add(of(ROOT, CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT));
            return entries;
        }

        if (rootNode.getType() != SchemaNodeType.OBJECT) {
            entries.add(of(ROOT, ROOT_NODE_SHOULD_BE_OBJECT));
            return entries;
        }

        if (rootNode.getSubType() != null) {
            entries.add(of(ROOT, ROOT_NODE_SHOULDNT_HAVE_SUBTUPES));
            return entries;
        }

        entries.addAll(rootNode.getSubNodes().stream().map(this::validateNode)
                .filter(Predicate.not(CollectionUtils::isEmpty))
                .flatMap(List::stream).collect(Collectors.toList()));

        return entries;

    }

    private List<SchemaValidationEntry> validateNode(SchemaNode node) {

        return validateNode(node.getName(), node, new ArrayList<>());
    }

    private List<SchemaValidationEntry> validateNode(String parentPath, SchemaNode node, List<SchemaValidationEntry> entries) {
        Optional.ofNullable(node.getType())
                .ifPresentOrElse(it -> checkNodeWithType(parentPath, node, entries),
                        () -> entries.add(of(parentPath, NODE_TYPE_NOT_DEFINED)));

        Optional.ofNullable(node.getSubNodes())
                .orElse(Collections.emptyList())
                .forEach(it -> validateNode(parentPath + "." + node.getName(), it, entries));
        return entries;
    }

    private void checkNodeWithType(String parentPath, SchemaNode node, List<SchemaValidationEntry> entries) {
        if (StringUtils.isEmpty(node.getName())) {
            entries.add(of(parentPath, NODE_NAME_NOT_DEFINED));
        }
        if (node.getType() == SchemaNodeType.ARRAY) {
            if (node.getSubType() == null || node.getSubType() == SchemaNodeType.UNDEFINED) {
                entries.add(of(parentPath, NODE_SUBTYPE_NOT_DEFINED));
            }
        } else {
            if (node.getSubType() != null) {
                entries.add(of(parentPath, NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED));
            }
        }
    }


    public static SchemaValidationEntry of(String path, String code) {
        return new SchemaValidationEntry().setPath(path).setCode(code);
    }

}
