package io.segmentme.redis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentStateChangedMessage extends RedisMessage {

    private final RedisMessageType type = RedisMessageType.SEGMENT_STATE_CHANGED;

    private String segmentId;

    private String contextId;

    private String integrationPointKey;

}
