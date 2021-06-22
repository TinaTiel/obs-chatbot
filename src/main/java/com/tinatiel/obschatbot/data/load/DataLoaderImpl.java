package com.tinatiel.obschatbot.data.load;

import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsClientSettingsDto;
import com.tinatiel.obschatbot.data.client.twitch.auth.TwitchClientAuthDataService;
import com.tinatiel.obschatbot.data.client.twitch.chat.TwitchClientChatDataService;
import com.tinatiel.obschatbot.data.system.SystemSettingsDataService;
import com.tinatiel.obschatbot.security.owner.OwnerDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;

public class DataLoaderImpl implements DataLoader {

  private final OwnerService ownerService;
  private final ObsClientDataService obsClientDataService;
  private final TwitchClientChatDataService twitchClientChatDataService;
  private final TwitchClientAuthDataService twitchClientAuthDataService;
  private final SystemSettingsDataService systemSettingsDataService;

  public DataLoaderImpl(OwnerService ownerService,
    ObsClientDataService obsClientDataService,
    TwitchClientChatDataService twitchClientChatDataService,
    TwitchClientAuthDataService twitchClientAuthDataService,
    SystemSettingsDataService systemSettingsDataService) {
    this.ownerService = ownerService;
    this.obsClientDataService = obsClientDataService;
    this.twitchClientChatDataService = twitchClientChatDataService;
    this.twitchClientAuthDataService = twitchClientAuthDataService;
    this.systemSettingsDataService = systemSettingsDataService;
  }

  @Override
  public void loadObsSettings() {
    OwnerDto ownerDto = ownerService.getOwner();
    obsClientDataService.findByOwner(ownerDto.getId()).ifPresentOrElse(
      (existing) -> {
      // do nothing to existing data
    }, () -> {
      obsClientDataService.save(ObsClientSettingsDto.builder()
        .owner(ownerDto.getId())
        .host("localhost")
        .port(4444)
        .password(null)
        .connectionTimeoutMs(5000)
        .build());
    });

  }

  @Override
  public void loadTwitchChatSettings() {

  }

  @Override
  public void loadTwitchAuthSettings() {

  }

  @Override
  public void loadSystemSettings() {

  }
}
