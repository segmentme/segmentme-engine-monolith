package io.segmentme.redis.dto.out;

import io.segmentme.redis.dto.RedisMessageType;
import io.segmentme.redis.dto.in.RedisMessageIn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentStateChangedMessageOut extends RedisMessageOut<SegmentStateChangedMessageOut.SegmentStateChanged> {

    private final RedisMessageType type = RedisMessageType.SEGMENT_STATE_CHANGED;

    @Data
    public static class SegmentStateChanged {

        private String segmentId;

        private String contextId;

        private String integrationPointKey;
    }
}
