package io.segmentme.redis.config;

import lombok.experimental.UtilityClass;
import org.springframework.data.redis.listener.ChannelTopic;

@UtilityClass
public class RedisTopicsBuilder {

    public final ChannelTopic ANALYSIS_REQUEST_TOPIC = ChannelTopic.of("analysisRequestTopic");

    private final String SEGMENT_CHANGED_TOPIC = "segmentChanged.%s";

    private final String ANALYSIS_RESULT = "analysisResultTopic.%s.%s.%s";

    public ChannelTopic buildAnalysisResponseTopic(String getIntegrationPointKey, String contextKey, String requesterId) {
        return ChannelTopic.of(String.format(ANALYSIS_RESULT, getIntegrationPointKey, contextKey, requesterId));
    }

    public ChannelTopic buildSegmentChangedTopic(String integrationPointKey) {
        return ChannelTopic.of(String.format(SEGMENT_CHANGED_TOPIC, integrationPointKey));
    }
}
