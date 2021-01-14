package io.segmentme.redis.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("connectedClient")
public class ConnectedClient implements Serializable {

    @Id
    private String id;

    private String integrationPointKey;

    private String clientId;

    private String sessionId;

}
