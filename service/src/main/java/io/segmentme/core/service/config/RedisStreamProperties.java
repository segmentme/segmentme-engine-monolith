package io.segmentme.core.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Data
@Component
@ConfigurationProperties("segmentme.application.redis.stream")
public class RedisStreamProperties {

    private String eventKey;

    private String groupName;

    private int streamPollTimeout;

    private String consumerName;

    @PostConstruct
    public void setConsumerName() throws UnknownHostException {
        consumerName = InetAddress.getLocalHost().getHostName() + UUID.randomUUID();
    }
}
