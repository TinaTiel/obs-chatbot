package com.tinatiel.obschatbot.core.client.twitch.auth;

import com.tinatiel.obschatbot.core.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

@Slf4j
public class TwitchAuthScheduler {

  private final OAuth2AuthorizedClientService authorizedClientService;
  private final OAuth2AuthorizedClientManager authorizedClientManager;

  public TwitchAuthScheduler(
    OAuth2AuthorizedClientService authorizedClientService,
    OAuth2AuthorizedClientManager authorizedClientManager) {
    this.authorizedClientService = authorizedClientService;
    this.authorizedClientManager = authorizedClientManager;
  }

  /**
   * Periodically check our token doesn't need to be refreshed. We cannot
   * wait for this to be handled normally by the web filters because the token
   * is used in our IRC client -- so, if the token needs to be refreshed, then we
   * must also stop/start the IRC client as well.
   */
//  @Scheduled(fixedRate = 1000*60*15)
  public void refreshTokenIfNeeded() {

    // Get the twitch client
    OAuth2AuthorizedClient twitchClient = authorizedClientService
      .loadAuthorizedClient("twitch", User.SYSTEM_PRINCIPAL_NAME);

    // If there was no authorized client, then log a warning TODO communicate/keep state to show user
    if(twitchClient == null) {
      // TODO do something to show to user
      log.warn("Twitch client hasn't been authorized");
      return;
    }

    // If the token will expire in the next 10 minutes, then refresh it
    authorizedClientManager.authorize(
      OAuth2AuthorizeRequest
        .withAuthorizedClient(twitchClient)
        .principal(User.SYSTEM_PRINCIPAL_NAME)
        .build()
    );

  }

}
