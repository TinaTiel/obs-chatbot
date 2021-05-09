package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.event.ClientConnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientDisconnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import net.twasi.obsremotejava.OBSRemoteController;
import org.pircbotx.PircBotX;

public class ObsClientFactory implements ClientFactory<OBSRemoteController, ObsClientSettings> {

  private final ObsClientSettingsFactory settingsFactory;
  private final ObsClientLifecycleGateway lifecycleGateway;

  public ObsClientFactory(
    ObsClientSettingsFactory settingsFactory,
    ObsClientLifecycleGateway lifecycleGateway) {
    this.settingsFactory = settingsFactory;
    this.lifecycleGateway = lifecycleGateway;
  }

  @Override
  public ObsClientDelegate generate() {
    ObsClientSettings settings = settingsFactory.getSettings();
    OBSRemoteController client = new OBSRemoteController(
      "ws://" + settings.getHost() + ":" + settings.getPort(),
      false,
      settings.getPassword(),
      false
    );

    // Define callbacks, we want to complete the future on connection
    client.registerOnError((message, throwable) -> {
      lifecycleGateway.submit(new ClientErrorEvent(message, throwable));
    });
    client.registerCloseCallback((statusCode, reason) -> {
      lifecycleGateway.submit(new ClientDisconnectedEvent(String.format(
        "OBS Client closed with statusCode %s and reason '%s", statusCode, reason
      )));
    });
    client.registerConnectionFailedCallback(message -> {
      lifecycleGateway.submit(new ClientErrorEvent("Connection to OBS failed: " + message));
    });
    client.registerConnectCallback(response -> {
      lifecycleGateway.submit(new ClientConnectedEvent());
    });

    return new ObsClientDelegate(client, settings);

  }
}
