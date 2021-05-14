/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.ClientManager;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsMessagingConfig;
import net.twasi.obsremotejava.OBSRemoteController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration for the OBS Client.
 */
@Import(ObsMessagingConfig.class)
@Configuration
public class ObsClientConfig {

  @Autowired
  ObsClientLifecycleGateway obsClientLifecycleGateway;
  @Value("${OBS_PASSWORD:na}")
  private String obsPassword;

  @Bean
  ObsClientSettingsFactory obsClientSettingsFactory() {
    String pass = obsPassword.equals("na") ? null : obsPassword;
    return new ObsClientSettingsFactory(new ObsClientSettings(
      "localhost", 4444, pass, 1000
    ));
  }

  @Bean
  ActionCommandConsumer<ObsClientDelegate> obsActionCommandConsumer() {
    return new ObsActionCommandConsumer(obsClientLifecycleGateway);
  }

  ClientFactory<OBSRemoteController, ObsClientSettings> obsClientFactory() {
    return new ObsClientFactory(obsClientSettingsFactory(), obsClientLifecycleGateway);
  }

  @Bean
  public ClientManager obsClientManager() {
    return new ObsClientManagerImpl(
      obsClientLifecycleGateway,
      obsClientFactory(),
      obsActionCommandConsumer()
    );
  }

}
