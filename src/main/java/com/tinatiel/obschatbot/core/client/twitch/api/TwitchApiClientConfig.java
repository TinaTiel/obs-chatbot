package com.tinatiel.obschatbot.core.client.twitch.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class TwitchApiClientConfig {

  //    @Autowired
//    OAuth2AuthorizedClientManager auth2AuthorizedClientManager;
//
//    @Bean
//    WebClient twitchWebClient() {
//        ServletOAuth2AuthorizedClientExchangeFilterFunction filterFunction =
//                new ServletOAuth2AuthorizedClientExchangeFilterFunction(auth2AuthorizedClientManager);
//
//        return WebClient.builder()
//                .apply(filterFunction.oauth2Configuration())
//                .build();
//    }
//

  @Autowired
  OAuth2AuthorizedClientService authorizedClientService;

  @Bean
  TwitchApiClient twitchApiClient() {
    return new TwitchApiClientImpl(authorizedClientService);
  }

}
