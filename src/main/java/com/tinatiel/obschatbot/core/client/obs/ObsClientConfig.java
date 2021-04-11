/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import net.twasi.obsremotejava.OBSRemoteController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ObsClientConfig {

  @Lazy
  @Bean
  public ObsClient obsClient() {
    OBSRemoteController controller = new OBSRemoteController("ws://localhost:4444", false, null,
      false);
    if (controller.isFailed()) {
      // handle failed connection
      System.out.println("FSDFSFSFSFSFFS");
    }
    return new ObsClientImpl(controller);
  }

}
