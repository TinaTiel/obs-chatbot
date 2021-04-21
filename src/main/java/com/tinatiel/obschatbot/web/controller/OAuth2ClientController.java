package com.tinatiel.obschatbot.web.controller;

import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
@RestController
public class OAuth2ClientController {

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @Autowired
  private TwitchAuthScheduler twitchAuthScheduler;

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
    twitchAuthScheduler.refreshTokenIfNeeded();
  }

}
