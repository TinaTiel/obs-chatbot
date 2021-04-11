/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.client.obs;

/**
 * Deprecated.
 */
public enum ObsClientState {
  ERROR,
  STOP_REQUESTED,
  STOPPING,
  STOPPED,
  START_REQUESTED,
  STARTING,
  CONNECTING,
  CONNECTED,
  AUTHENTICATING,
  AUTHENTICATED,
  READY
}
