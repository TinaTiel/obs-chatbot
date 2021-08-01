package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/settings")
@RestController
@RequiredArgsConstructor
public class SettingsController {

  private final OwnerService ownerService;
  private final SystemSettingsDataService systemSettingsDataService;
  private final ObsClientDataService obsClientDataService;
  private final TwitchClientAuthDataService twitchClientAuthDataService;

  private static final String SYSTEM = "/system";
  private static final String OBS = "/obs";
  private static final String TWITCH = "/twitch";

  @GetMapping(SYSTEM)
  ResponseEntity<SystemSettingsDto> getSystemSettings() {
    return systemSettingsDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping(SYSTEM)
  ResponseEntity<Void> saveSystemSettings(@RequestBody SystemSettingsDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    systemSettingsDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

  @GetMapping(OBS)
  ResponseEntity<ObsClientSettingsDto> getOBsSettings() {
    return obsClientDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping(OBS)
  ResponseEntity<Void> saveObsSettings(@RequestBody ObsClientSettingsDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    obsClientDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

  @GetMapping(TWITCH + "/auth")
  ResponseEntity<TwitchClientAuthDataDto> getTwitchAuthSettings() {
    return twitchClientAuthDataService.findByOwner(ownerService.getOwner().getId())
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping(TWITCH + "/auth")
  ResponseEntity<Void> saveTwitchAuthSettings(@RequestBody TwitchClientAuthDataDto settingsDto) {
    settingsDto.setOwner(ownerService.getOwner().getId());
    twitchClientAuthDataService.save(settingsDto);
    return ResponseEntity.ok(null);
  }

}
