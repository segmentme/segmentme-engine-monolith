package io.segmentme.core.db.domain.statistic;

import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "analyzedData")
public class AnalyzedData extends DbObject {
    private String payload;

    @Indexed
    private String workspaceId;

    @Indexed
    private String integrationPointKey;

    private List<String> analyzedSegments;

    @Indexed(unique = true)
    private String hash;

    private Map<String, List<Object>> nodeValues;

    @Indexed
    private String clientId;

    public String buildHash() {
        return String.valueOf((clientId + ":" + integrationPointKey + ":" + payload.hashCode()).hashCode());
    }
}
