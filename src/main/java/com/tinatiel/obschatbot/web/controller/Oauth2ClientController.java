package com.tinatiel.obschatbot.web.controller;

import com.tinatiel.obschatbot.core.client.twitch.api.TwitchApiClient;
import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthValidationService;
import com.tinatiel.obschatbot.core.messaging.ObsChatbotEvent;
import java.util.concurrent.BlockingQueue;
import javax.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a test controller, replace or delete me when ready.
 */
@Slf4j
@RestController
public class Oauth2ClientController {

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  private TwitchAuthValidationService twitchAuthValidationService;

  @Autowired
  BlockingQueue<ObsChatbotEvent> twitchAuthAuditQueue;

  @Autowired
  TwitchApiClient twitchApiClient;

  @GetMapping("/test/twitch")
  public String index() {
    ClientRegistration clientRegistration = clientRegistrationRepository
        .findByRegistrationId("twitch");

    OAuth2AuthorizedClient authorizedClient = authorizedClientService
        .loadAuthorizedClient("twitch", "SYSTEM");

    return "Access Token: " + authorizedClient.getAccessToken().getTokenValue() + System.lineSeparator()
      + "Expiring: " + authorizedClient.getAccessToken().getExpiresAt() + System.lineSeparator()
      + "Refresh Token: " + authorizedClient.getRefreshToken().getTokenValue() + System.lineSeparator()
      + "Expiring: " + authorizedClient.getRefreshToken().getExpiresAt();

  }

  @PostMapping("/refresh/twitch")
  public void refresh() {
    twitchAuthValidationService.refreshTokenIfNeeded();
  }

  @GetMapping("/validate/twitch")
  public String validate() {
    twitchAuthValidationService.validateToken();
    return twitchAuthAuditQueue.toString();
  }

  @GetMapping("/following")
  public boolean isFollowing(@PathParam("broadcasterId") String broadcasterId, @PathParam("viewerId") String viewerId) {
    log.debug("Checking if viewer (" + viewerId + ") is following broadcaster (" + broadcasterId + ")");
    return twitchApiClient.isFollowing(broadcasterId, viewerId);
  }

  @GetMapping("/userid")
  public String getUserId(@PathParam("username") String username) {
    return twitchApiClient.getUserIdFromUsername(username);
  }


}
