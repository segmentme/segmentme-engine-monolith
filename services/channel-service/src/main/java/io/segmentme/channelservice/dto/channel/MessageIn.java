package io.segmentme.channelservice.dto.channel;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SdkAnalysisMessageIn.class, name = "ANALYSIS_REQUEST"),
})
public abstract class MessageIn implements Serializable {

    @JsonIgnore
    public abstract MessageType getType();

    @JsonIgnore
    private LocalDateTime eventDate = LocalDateTime.now();
}
