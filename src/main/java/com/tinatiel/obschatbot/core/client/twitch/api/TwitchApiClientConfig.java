package com.tinatiel.obschatbot.core.client.twitch.api;

import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthConnectionSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.RestTemplate;

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

  @Autowired
  TwitchAuthConnectionSettingsFactory twitchAuthConnectionSettingsFactory;

  @Bean
  RestTemplate twitchApiClientRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    return restTemplate;
  }

  @Autowired
  TwitchApiClientSettingsFactory twitchApiClientSettingsFactory;

  @Bean
  TwitchApiClient twitchApiClient() {

    return new TwitchApiClientImpl(
      twitchApiClientRestTemplate(),
      authorizedClientService,
      twitchAuthConnectionSettingsFactory,
      twitchApiClientSettingsFactory
    );
  }

}
