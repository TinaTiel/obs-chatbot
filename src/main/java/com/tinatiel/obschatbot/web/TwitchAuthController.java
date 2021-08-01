package com.tinatiel.obschatbot.web;

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

}
