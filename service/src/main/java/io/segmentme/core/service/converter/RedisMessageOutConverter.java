package io.segmentme.core.service.converter;

import io.segmentme.core.service.dto.analysis.SdkAnalysisResponse;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.redis.dto.AnalysisResponse;
import io.segmentme.redis.dto.AnalysisResult;
import io.segmentme.redis.dto.out.SdkAnalysisResponseMessageOut;
import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;

@UtilityClass
public class RedisMessageOutConverter {

    public SdkAnalysisResponseMessageOut of(SdkAnalysisResponse response) {
        SdkAnalysisResponseMessageOut messageOut = new SdkAnalysisResponseMessageOut();
        messageOut.setBody(new AnalysisResponse()
                .setContextId(response.getContextId())
                .setAnalyzedSegments(response.getAnalyzedSegments().stream().map(RedisMessageOutConverter::of).collect(Collectors.toList())));
        return messageOut;
    }

    private AnalysisResult of(SegmentAnalysisResult result) {
        return new AnalysisResult()
                .setAnalysisTime(result.getAnalysisTime())
                .setName(result.getName())
                .setValue(result.isValue());
    }
}
