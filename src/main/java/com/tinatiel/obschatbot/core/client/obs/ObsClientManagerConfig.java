/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Encompasses all configuration for the OBS Client Manager.
 */
@Configuration
public class ObsClientManagerConfig {

  private ObsSettings obsSettings() {
    return new ObsSettings("localhost", 4444, null, 1000);
  }

  @Bean
  public ClientManager obsClientManager() {
    return new ObsClientManagerImpl(obsSettings());
  }

}
