package io.segmentme.core.db.domain.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "context")
public class AnalysisContext extends DbObject{
    private SchemaNode rootNode;
}
