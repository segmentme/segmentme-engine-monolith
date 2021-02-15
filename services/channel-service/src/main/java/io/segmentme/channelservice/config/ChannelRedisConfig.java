package io.segmentme.channelservice.config;

import io.segmentme.redis.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ChannelRedisConfig extends RedisConfig {

}
