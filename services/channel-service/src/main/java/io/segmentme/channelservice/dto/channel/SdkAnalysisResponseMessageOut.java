package io.segmentme.channelservice.dto.channel;

import io.segmentme.channelservice.dto.SdkAnalysisResponse;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class SdkAnalysisResponseMessageOut extends MessageOut<SdkAnalysisResponse> {

    private final MessageType type = MessageType.ANALYSIS_RESULT;

    public SdkAnalysisResponseMessageOut(SdkAnalysisResponse body) {
        super(body);
    }
}
