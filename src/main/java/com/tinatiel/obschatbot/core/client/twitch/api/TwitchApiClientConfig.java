package com.tinatiel.obschatbot.core.client.twitch.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  @Bean
  TwitchApiClient twitchApiClient() {
    return new TwitchApiClient() {
      @Override
      public boolean isFollowing(String broadcasterId, String viewerId) {
        return false;
      }
    };
  }

}
