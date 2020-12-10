package io.segmentme.core.db.domain.statistic;

import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "analyzed-data")
public class AnalyzedData extends DbObject {
    private String payload;

    @Indexed(unique = true)
    private String hash;

    private Map<String, Object> nodeValues;

    public void setPayload(String payload) {
        this.payload = payload;
        this.hash = String.valueOf(payload.hashCode());
    }
}
