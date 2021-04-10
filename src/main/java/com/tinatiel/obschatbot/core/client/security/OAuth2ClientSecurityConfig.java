package com.tinatiel.obschatbot.core.client.security;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrations;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class OAuth2ClientSecurityConfig {


    // TODO get from DB / settings factory
    private String twitchClientId = "drk7vknesfqclm67yywgo15qkxzvij";
    private String twitchClientSecret = "hhzyuo32i6q2k4nnr458i80wzn4kph";

    // TODO build from configured scheme, host, and port
    private final String twitchClientRedirectUri = "http://localhost:8080/" + "authorized/twitch";
    // initiate auth at /oauth2/authorization/twitch

    ClientRegistration twitchOAuth2ClientRegistration() {

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("twitch")
                .authorizationUri("https://id.twitch.tv/oauth2/authorize")
                .tokenUri("https://id.twitch.tv/oauth2/token")
                .clientId(twitchClientId)
                .clientSecret(twitchClientSecret)
                .redirectUri(twitchClientRedirectUri)
                .scope("channel:moderate", "chat:read", "chat:edit")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
                .build();

        return clientRegistration;
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {

        List<ClientRegistration> clientRegistrations = new ArrayList<>();
        clientRegistrations.add(twitchOAuth2ClientRegistration());

        ClientRegistrationRepository clientRegistrationRepository =
                new InMemoryClientRegistrationRepository(clientRegistrations);

        return clientRegistrationRepository;
    }

    @Bean
    OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
        OAuth2AuthorizedClientService clientService =
                new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
        return clientService;
    }

    @Bean
    OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        OAuth2AuthorizedClientRepository authorizedClientRepository =
                new SystemPrincipalOAuth2AuthorizedClientRepository(oAuth2AuthorizedClientService());
        return  authorizedClientRepository;
    }

//    @Bean
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
                        oAuth2AuthorizedClientService()
                );
        clientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);

//        clientManager.setAuthorizationSuccessHandler(new OAuth2AuthorizationSuccessHandler() {
//
//            @Override
//            public void onAuthorizationSuccess(OAuth2AuthorizedClient oAuth2AuthorizedClient, Authentication authentication, Map<String, Object> map) {
//                log.warn("HOWDY");
//                log.warn("principal: " + oAuth2AuthorizedClient.getPrincipalName());
//                log.warn("token: " + oAuth2AuthorizedClient.getAccessToken());
//                log.warn("authentication: " + authentication);
//                oAuth2AuthorizedClientService().saveAuthorizedClient(oAuth2AuthorizedClient, authentication);
//            }
//        });
//
//        clientManager.setAuthorizationFailureHandler(new OAuth2AuthorizationFailureHandler() {
//            @Override
//            public void onAuthorizationFailure(OAuth2AuthorizationException e, Authentication authentication, Map<String, Object> map) {
//
//                log.debug("foo");
//            }
//        });

        return clientManager;
    }

}
