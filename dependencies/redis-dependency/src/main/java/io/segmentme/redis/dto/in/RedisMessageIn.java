package io.segmentme.redis.dto.in;

import com.fasterxml.jackson.annotation.*;
import io.segmentme.redis.dto.RedisMessage;
import io.segmentme.redis.dto.RedisMessageType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SdkAnalysisMessageIn.class, name = "ANALYSIS_MESSAGE_IN")
})
public abstract class RedisMessageIn<B> implements RedisMessage, Serializable {

    @Valid
    @NotNull
    private B body;

    @JsonIgnore
    public abstract RedisMessageType getType();

    private Instant eventDate = Instant.now();

}
