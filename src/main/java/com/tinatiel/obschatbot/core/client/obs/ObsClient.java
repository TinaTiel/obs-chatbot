/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ActionClient;

/**
 * Deprecated.
 */
public interface ObsClient extends ActionClient {

  void connect();

  void setSourceVisibility(String scene, String source, boolean visibility);

}
