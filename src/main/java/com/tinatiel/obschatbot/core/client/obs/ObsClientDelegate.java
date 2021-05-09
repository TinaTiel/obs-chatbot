package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import net.twasi.obsremotejava.OBSRemoteController;
import org.pircbotx.PircBotX;

public class ObsClientDelegate implements ClientDelegate<OBSRemoteController, ObsClientSettings> {

  private final OBSRemoteController client;
  private final ObsClientSettings settings;

  public ObsClientDelegate(OBSRemoteController client,
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
