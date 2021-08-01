package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.core.client.twitch.auth.TwitchAuthClient;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/twitch/auth")
@RestController
@RequiredArgsConstructor
public class TwitchAuthController {

  private final OwnerService ownerService;
  private final TwitchClientAuthDataService twitchClientAuthDataService;
  private final TwitchAuthClient twitchAuthClient;

  @GetMapping("/settings")
  ResponseEntity<TwitchClientAuthDataDto> getTwitchAuthSettings() {
    return twitchClientAuthDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/settings")
  ResponseEntity<Void> saveTwitchAuthSettings(@RequestBody TwitchClientAuthDataDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    twitchClientAuthDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/status")
  ResponseEntity<Void> checkAuthStatus() {
    boolean tokenIsValid = twitchAuthClient.isCurrentAccessTokenValid();
    if(tokenIsValid) {
      return ResponseEntity.ok(null);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

}
