package io.segmentme.core.api.config.security.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("segmentme.security")
public class SecuritySettings {

    private Resource keyStore;

    private String password;

    private String jwtAlias;

    private JWT jwt;

    private String passwordSecret;

    @Data
    public static class JWT {

        private Validity validity;

        private String verifierKey;

        @Data
        public static class Validity {

            private Integer access = 150;

            private Integer refresh = 200;

        }
    }

}
