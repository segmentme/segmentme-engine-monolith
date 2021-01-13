package io.segmentme.channelservice.dto;

import lombok.*;
import org.springframework.messaging.rsocket.RSocketRequester;

@Data
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(exclude = "requester")
public class RSocketSessionHolder {

    private final String id;

    private final RSocketRequester requester;
}
