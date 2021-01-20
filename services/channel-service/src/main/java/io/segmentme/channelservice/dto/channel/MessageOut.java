package io.segmentme.channelservice.dto.channel;

import com.fasterxml.jackson.annotation.*;
import io.segmentme.channelservice.dto.SdkAnalysisResponse;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SegmentStateChangedMessageOut.class, name = "SEGMENT_STATE_CHANGED"),
        @JsonSubTypes.Type(value = SdkAnalysisResponseMessageOut.class, name = "ANALYSIS_RESULT"),
})
public abstract class MessageOut<B> implements Serializable {

    public B body;

    @JsonIgnore
    public abstract MessageType getType();

    @JsonIgnore
    private LocalDateTime eventDate = LocalDateTime.now();

    public MessageOut(B body) {
        this.body = body;
    }
}
