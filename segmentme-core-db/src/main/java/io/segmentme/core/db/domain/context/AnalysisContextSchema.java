package io.segmentme.core.db.domain.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context")
public class AnalysisContextSchema extends DbObject {
    private SchemaNode rootNode;

    private Map<String, SchemaNodeType> inlinePath;
}
