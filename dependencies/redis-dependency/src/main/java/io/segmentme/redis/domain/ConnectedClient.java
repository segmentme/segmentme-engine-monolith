package io.segmentme.redis.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@RedisHash("connectedClient")
public class ConnectedClient implements Serializable {

    @Id
    private String id;

    @Indexed
    private String integrationPointKey;

    @Indexed
    private String clientId;

    @Indexed
    private String sessionId;

}
