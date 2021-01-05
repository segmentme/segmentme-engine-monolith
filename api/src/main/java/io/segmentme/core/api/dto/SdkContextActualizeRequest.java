package io.segmentme.core.api.dto;

import lombok.Data;

@Data
public class SdkContextActualizeRequest {
    private String rawPayload;

    private String contextKey;


}
