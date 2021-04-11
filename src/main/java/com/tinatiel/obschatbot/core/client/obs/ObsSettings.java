/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Encompasses the connection settings required to initiate connection with OBS.
 */
@Data
@AllArgsConstructor
@ToString
public class ObsSettings {

  private String host;
  private int port;
  @ToString.Exclude
  private String password;
  private long connectionTimeoutMs;

}
