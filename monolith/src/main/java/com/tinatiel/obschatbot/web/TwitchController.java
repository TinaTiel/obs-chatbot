package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import com.tinatiel.obschatbot.web.model.ClientStatusRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/twitch")
@RestController
@RequiredArgsConstructor
public class TwitchController {

  private final OwnerService ownerService;
  private final TwitchClientChatDataService twitchClientChatDataService;
  private final ClientManager twitchChatClientManager;

  @GetMapping("/settings")
  ResponseEntity<TwitchClientChatDataDto> getTwitchSettings() {
    return twitchClientChatDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/settings")
  ResponseEntity<Void> saveTwitchSettings(@RequestBody TwitchClientChatDataDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    twitchClientChatDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

  @PutMapping("/status")
  ResponseEntity<Void> saveObsStatus(@RequestBody ClientStatusRequestDto statusRequest) {
    if(statusRequest != null) {
      switch (statusRequest.getState()) {
        case START:
          twitchChatClientManager.startClient();
          break;
        case STOP:
          twitchChatClientManager.stopClient();
          break;
        case RESTART:
          twitchChatClientManager.reloadClient();
          break;
      }
    }
    return ResponseEntity.ok(null);
  }

}