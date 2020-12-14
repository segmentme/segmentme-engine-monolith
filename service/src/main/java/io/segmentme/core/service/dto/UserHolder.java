package io.segmentme.core.service.dto;

import lombok.Data;

@Data
public class UserHolder {
    private String id;

    private String externalId;

    private String email;

    private String name;

    private String lastActiveWorkspace;
}
