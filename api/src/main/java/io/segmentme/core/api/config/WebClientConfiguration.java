package io.segmentme.core.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    @Value("${auth0.managementApi}")
    private String managementApi;

    @Value("${auth0.audience}")
    private String audience;

    @Bean("auth0-client")
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId("auth0");

        return WebClient.builder()
                .baseUrl(managementApi)
                .apply(oauth2.oauth2Configuration())
                .build();

    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder
                        .builder()
                        .clientCredentials(clientCredentialsGrantBuilder -> {
                            DefaultClientCredentialsTokenResponseClient responseClient = new DefaultClientCredentialsTokenResponseClient();
                            responseClient.setRequestEntityConverter(new OAuth2ClientCredentialsGrantRequestEntityConverter() {
                                @Override
                                public RequestEntity<?> convert(OAuth2ClientCredentialsGrantRequest clientCredentialsGrantRequest) {
                                    RequestEntity<?> convert = super.convert(clientCredentialsGrantRequest);
                                    MultiValueMap<String, String> httpEntity = (MultiValueMap<String, String>) convert.getBody();
                                    httpEntity.put("audience", Collections.singletonList(audience));
                                    return convert;
                                }
                            });
                            clientCredentialsGrantBuilder.accessTokenResponseClient(responseClient);
                        }).build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
