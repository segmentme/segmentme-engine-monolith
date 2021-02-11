package io.segmentme.redis.dto.out;

import com.fasterxml.jackson.annotation.*;
import io.segmentme.redis.dto.*;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SegmentStateChangedMessageOut.class, name = "SEGMENT_STATE_CHANGED"),
        @JsonSubTypes.Type(value = SdkAnalysisResponseMessageOut.class, name = "ANALYSIS_RESULT")
})
public abstract class RedisMessageOut<B> implements RedisMessage, Serializable {

    private B body;

    @JsonIgnore
    public abstract RedisMessageType getType();

    private Instant eventDate = Instant.now();
}
