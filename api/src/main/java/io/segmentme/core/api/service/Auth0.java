package io.segmentme.core.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class Auth0 {
    private final WebClient auth0WebClient;

    public void acknowledge(String userId) {
        log.info("Acknowledge request");
        HashMap<String, Object> appMetadata = new HashMap<>();
        appMetadata.put("acknowledged", true);

        auth0WebClient.patch().uri("/users/" + userId)
                .bodyValue(new AppMetadata().setAppMetadata(appMetadata))
                .exchange()
                .doOnSuccess(it -> log.info("Acknowledge result {}", it.body(BodyExtractors.toMono(String.class)))).subscribe();

    }


    @Data
    public static class AppMetadata {

        @JsonProperty("app_metadata")
        public Map<String, Object> appMetadata;
    }

}
