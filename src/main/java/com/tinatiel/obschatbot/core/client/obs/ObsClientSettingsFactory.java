/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientSettingsFactory;
import com.tinatiel.obschatbot.data.client.obs.ObsClientDataService;
import com.tinatiel.obschatbot.data.client.obs.model.ObsSettingsDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;

/**
 * Provides up-to-date settings for the OBS Client.
 */
public class ObsClientSettingsFactory implements ClientSettingsFactory<ObsClientSettings> {

  private final OwnerService ownerService;
  private final ObsClientDataService dataService;

  public ObsClientSettingsFactory(OwnerService ownerService,
    ObsClientDataService dataService) {
    this.ownerService = ownerService;
    this.dataService = dataService;
  }

  @Override
  public ObsClientSettings getSettings() {
    ObsSettingsDto settings = dataService.findByOwner(ownerService.getOwner().getId())
      .orElseThrow(() -> new IllegalStateException("Could not retrieve OBS settings"));
    return ObsClientSettings.builder()
      .host(settings.getHost())
      .port(settings.getPort())
      .password(settings.getPassword())
      .connectionTimeoutMs(settings.getConnectionTimeoutMs())
      .build();
  }
}
