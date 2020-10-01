package io.segmentme.core.api.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUser {

    @JsonAlias("sub")
    private String id;

    @JsonAlias("https://segmentme.io:fullName")
    private String fullName;

    @JsonAlias("https://segmentme.io:email")
    private String email;

    @JsonAlias("https://segmentme.io:acknowledged")
    private boolean acknowledged;
}
