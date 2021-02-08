package io.segmentme.channelservice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("segmentme.rsocket")
@Data
public class SegmentMeRsocketConfiguration {

    private LeaseSettings lease;


    @Data
    public static class VegasSettings {
        private int initialLimit;
        private int maxConcurrency;
    }

    @Data
    public static class LeaseSettings {
         private int capacity;
         private int ttl;
         private VegasSettings vegasLimit;
    }


}
