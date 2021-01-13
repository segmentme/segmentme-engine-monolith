package io.segmentme.redist.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("segmentme.application.redis")
public class RedisProperties {

    private int port;

    private int connectionTimeout;

    private int readTimeOut;

    private String host;
}
