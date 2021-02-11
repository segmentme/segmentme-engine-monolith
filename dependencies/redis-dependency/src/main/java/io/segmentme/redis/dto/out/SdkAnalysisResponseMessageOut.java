package io.segmentme.redis.dto.out;

import io.segmentme.redis.dto.AnalysisResponse;
import io.segmentme.redis.dto.RedisMessageType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SdkAnalysisResponseMessageOut extends RedisMessageOut<AnalysisResponse> {

    private final RedisMessageType type = RedisMessageType.ANALYSIS_RESULT;

}
