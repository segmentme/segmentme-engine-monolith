package io.segmentme.redis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReanalysisMessage extends RedisMessage {

    private final RedisMessageType type = RedisMessageType.REANALYIS;

    private String segmentId;

    private String contextId;

    private String integrationPointKey;

}
