package io.segmentme.redis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReanalysisMessage extends RedisMessage {
    private String segmentId;

    private String integrationPointKey;

    @Override
    public RedisMessageType type() {
        return RedisMessageType.REANALYIS;
    }
}
