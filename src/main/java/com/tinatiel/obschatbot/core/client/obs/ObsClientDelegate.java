package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import net.twasi.obsremotejava.OBSRemoteController;

/**
 * A client delegate providing a reference to the instance of the Twasi OBS Remote Controller
 * and the settings provided at the time of instantiation.
 */
public class ObsClientDelegate implements ClientDelegate<OBSRemoteController, ObsClientSettings> {

  private final OBSRemoteController client;
  private final ObsClientSettings settings;

  public ObsClientDelegate(
      OBSRemoteController client,
      ObsClientSettings settings) {
    this.client = client;
    this.settings = settings;
  }

  @Override
  public OBSRemoteController getClient() {
    return client;
  }

  @Override
  public ObsClientSettings getSettings() {
    return settings;
  }
}
