package io.segmentme.redis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SegmentStateChangedMessage.class, name = "SEGMENT_STATE_CHANGED"),
})
public abstract class RedisMessage implements Serializable {

    @JsonIgnore
    public abstract RedisMessageType getType();

    private Instant eventDate = Instant.now();

}
