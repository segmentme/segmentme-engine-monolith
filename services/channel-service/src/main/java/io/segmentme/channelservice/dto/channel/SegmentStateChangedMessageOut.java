package io.segmentme.channelservice.dto.channel;

import io.segmentme.redis.dto.SegmentStateChangedMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentStateChangedMessageOut extends MessageOut<SegmentStateChangedMessage> {

    private final MessageType type = MessageType.SEGMENT_STATE_CHANGED;

    public SegmentStateChangedMessageOut(SegmentStateChangedMessage body) {
        super(body);
    }
}
