package io.segmentme.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDetails {
    private UserBasicInfo userBasicInfo;
    private List<CurrentUserProfile> profiles;
}
