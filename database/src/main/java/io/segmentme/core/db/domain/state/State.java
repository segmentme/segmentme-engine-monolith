package io.segmentme.core.db.domain.state;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.db.domain.context.DbObject;
import io.segmentme.core.db.domain.segment.Segment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("state")
@EqualsAndHashCode(callSuper = true)
public class State extends DbObject {

    private String name;

    private JsonNode value;

    private Segment segment;

    @Indexed
    private String workSpaceId;

    @Indexed
    private String integrationPoint;

    @Indexed
    private String contextId;

}
