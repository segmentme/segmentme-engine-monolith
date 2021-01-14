package io.segmentme.core.service.analysis.event.processor;

import io.segmentme.redis.dto.ReanalysisMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalysisEventProcessor {

    @EventListener(ReanalysisMessage.class)
    public void handleMessage(ReanalysisMessage redisMessage) {
        log.info("got it! {}", redisMessage);
    }
}
