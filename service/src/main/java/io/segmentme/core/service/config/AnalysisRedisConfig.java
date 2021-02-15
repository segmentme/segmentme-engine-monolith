package io.segmentme.core.service.config;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.*;
import io.segmentme.redis.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamReceiver;

import java.net.UnknownHostException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnalysisRedisConfig extends RedisConfig {

    private final static String REDIS_STREAM_TYPE = "MKSTREAM";

    @Autowired
    private RedisStreamProperties redisStreamProperties;

    @Bean
    public StreamReceiver<String, MapRecord<String, Object, Object>> streamReceiver(RedisTemplate<String, Object> redisTemplate, LettuceConnectionFactory connectionFactory) throws UnknownHostException {
        this.initRedisStream(redisTemplate);

        var jsonRedisSerializer = super.prepareJackson2JsonRedisSerializer();

        StreamReceiver.StreamReceiverOptions<String, MapRecord<String, Object, Object>> object = StreamReceiver.StreamReceiverOptions.builder()
                .hashKeySerializer(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
                .keySerializer(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .hashValueSerializer(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer))
                .build();

        return StreamReceiver.create(connectionFactory, object);
    }

    @SuppressWarnings("unchecked")
    private void initRedisStream(RedisTemplate<String, Object> redisTemplate) {
        try {
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(redisStreamProperties.getEventKey()))) {
                log.info("{} does not exist. Creating stream with the consumer group", redisStreamProperties.getEventKey());

                RedisAsyncCommands<String, String> commands = (RedisAsyncCommands<String, String>) redisTemplate.getConnectionFactory()
                        .getConnection()
                        .getNativeConnection();

                CommandArgs<String, String> args = new CommandArgs<>(StringCodec.UTF8)
                        .add(CommandKeyword.CREATE)
                        .add(redisStreamProperties.getEventKey())
                        .add(redisStreamProperties.getGroupName())
                        .add("0")
                        .add(REDIS_STREAM_TYPE);

                commands.dispatch(CommandType.XGROUP, new StatusOutput<>(StringCodec.UTF8), args);
            } else {
                redisTemplate.opsForStream().createGroup(redisStreamProperties.getEventKey(), ReadOffset.latest(), redisStreamProperties.getGroupName());
            }
        } catch (Exception ex) {
            log.info("Consumer group already present: {}", redisStreamProperties.getGroupName());
        }
    }
}
