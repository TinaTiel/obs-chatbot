package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.client.security.SystemPrincipalOAuth2AuthorizedClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "com.tinatiel.twitch")
@Slf4j
@Configuration
public class TwitchOAuth2ClientConfig {

    // TODO get from DB / settings factory
    @Value("${TWITCH_CLIENT_ID:noclientspecified}")
    private String twitchClientId;

    @Value("${TWITCH_CLIENT_SECRET:nosecretspecified}")
    private String twitchClientSecret;

    // TODO build from configured scheme, host, and port
    // initiate auth at /oauth2/authorization/twitch
    private final String twitchClientRedirectUri = "http://localhost:8080/" + "authorized/twitch";

    /**
     * manages **authorized** clients
     * TODO Replace with JdbcOAuth2AuthorizedClientService
     * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/oauth2/client/JdbcOAuth2AuthorizedClientService.html
     */
    @Bean
    OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    OAuth2AuthorizedClientManager auth2AuthorizedClientManager() {

        // support auth code and refresh token
        OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();

        // Twitch requires periodic calls to verify token validity for user-token API usage
        // (https://dev.twitch.tv/docs/authentication#validating-requests), so we must use
        // the AuthorizedClientServiceOAuth2AuthorizedClientManager because it supports making requests outside
        // the HTTP context (e.g. via scheduled calls for example)
        AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository(),
                        authorizedClientService()
                );
        clientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);

        return clientManager;
    }

    /**
     * Stores the client settings for each client we define, for example the Twitch client id, secret, auth url, etc.
     */
    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {

        List<ClientRegistration> clientRegistrations = new ArrayList<>();
        clientRegistrations.add(twitchOAuth2ClientRegistration());

        ClientRegistrationRepository clientRegistrationRepository =
                new InMemoryClientRegistrationRepository(clientRegistrations); // TODO: Swap with our own impl that uses JDBC / JPA

        return clientRegistrationRepository;
    }

    /**
     * Responsible for persisting **Authorized** Clients; e.g. Clients that are associated with an Access Token + Principal
     * In our case, we use our own implementation that persists all requests under the SYSTEM user since all access
     * is entirely local and therefore all is anonymous (no Principal)
     */
    @Bean
    OAuth2AuthorizedClientRepository authorizedClientRepository() {
        OAuth2AuthorizedClientRepository authorizedClientRepository =
                new SystemPrincipalOAuth2AuthorizedClientRepository(authorizedClientService());
        return  authorizedClientRepository;
    }

    /**
     * Define the ClientRegistration for the Twitch Client.
     * TODO: Move this into a custom ClientRegistrationRepository so it can be rebuilt at runtime
     */
    private ClientRegistration twitchOAuth2ClientRegistration() {

        TwitchAuthConnectionSettings settings = twitchAuthConnectionSettings;

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("twitch")
                .authorizationUri(settings.getHost() + settings.getAuthorizationPath())
                .tokenUri(settings.getHost() + settings.getTokenPath())
                .clientId(twitchClientId)
                .clientSecret(twitchClientSecret)
                .redirectUri(twitchClientRedirectUri)
                .scope(settings.getScopes())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .build();

        return clientRegistration;
    }

//    @Bean
//    TwitchAuthConnectionSettings twitchAuthConnectionSettings() {
//        return TwitchAuthConnectionSettings.builder()
//                .host("https://id.twitch.tv")
//                .authorizationPath("/oauth2/authorize")
//                .tokenPath("/oauth2/token")
//                .scopes(Arrays.asList("channel:moderate", "chat:read", "chat:edit"))
//                .build();
//    }
    @Autowired
    TwitchAuthConnectionSettings twitchAuthConnectionSettings;

}