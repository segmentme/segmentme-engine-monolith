package io.segmentme.core.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Auth0Service {

    private final static String USER_URL = "/users/";

    private final static String VERIFICATION_EMAIL_URL = "/jobs/verification-email";


    private final WebClient auth0WebClient;

    public void acknowledge(String userId) {
        log.info("Acknowledge request");

        auth0WebClient.patch()
                .uri(USER_URL + userId)
                .bodyValue(AppMetadata.of(Map.of("acknowledged", true)))
                .exchange()
                .doOnSuccess(response -> log.info("Acknowledge response status {}", response.statusCode()))
                .flatMap(response -> response.bodyToMono(String.class))
                .doOnSuccess(response -> log.info("Acknowledge result {}", response))
                .subscribe();
    }

    public void resendVerificationEmail(String userId) {
        log.info("Resend verification email request");

        auth0WebClient.post().uri(VERIFICATION_EMAIL_URL)
                .bodyValue(Map.of("user_id", userId))
                .exchange()
                .doOnSuccess(response -> log.info("Resend verification email response status: {}", response.statusCode()))
                .flatMap(response -> !response.statusCode().is2xxSuccessful() ? Mono.error(new RuntimeException("Error sending email")) : response.bodyToMono(String.class))
                .doOnSuccess(response -> log.info("Resend verification email result {}", response))
                .block();
    }


    @Data
    @AllArgsConstructor(staticName = "of")
    public static class AppMetadata {

        @JsonProperty("app_metadata")
        public Map<String, Object> appMetadata;
    }
}
