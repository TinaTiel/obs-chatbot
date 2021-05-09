package com.tinatiel.obschatbot.core.client.obs;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import com.tinatiel.obschatbot.core.client.ClientFactory;
import com.tinatiel.obschatbot.core.client.event.ClientAuthenticatedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientConnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientDisconnectedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStopRequestedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppedEvent;
import com.tinatiel.obschatbot.core.client.event.ClientStoppingEvent;
import com.tinatiel.obschatbot.core.client.obs.messaging.ObsClientLifecycleGateway;
import com.tinatiel.obschatbot.core.client.twitch.chat.TwitchChatClientSettings;
import lombok.extern.slf4j.Slf4j;
import net.twasi.obsremotejava.OBSRemoteController;
import org.pircbotx.PircBotX;

@Slf4j
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
      log.isDebugEnabled(),
      settings.getPassword(),
      false
    );

    // Define callbacks, we want to complete the future on connection
    client.registerOnError((message, throwable) -> {
      log.debug("OnError: " + message, throwable);
      lifecycleGateway.submit(new ClientErrorEvent(message, throwable));
    });

    // Close will be called when OBS can be reached, but authentication is invalid
    client.registerCloseCallback((statusCode, reason) -> {
      log.debug("onClose: status=" + statusCode + ", reason=" + reason);
      lifecycleGateway.submit(new ClientDisconnectedEvent(String.format(
        "OBS Client closed with statusCode %s and reason '%s", statusCode, reason
      )));
    });
    client.registerConnectionFailedCallback(message -> {
      log.debug("onConnectionFailed: " + message);
      if(message != null & message.toLowerCase().contains("failed to connect to obs")) {
        lifecycleGateway.submit(new ClientStopRequestedEvent(message));
        lifecycleGateway.submit(new ClientStoppingEvent());
        lifecycleGateway.submit(new ClientDisconnectedEvent(message)); // since the client doesn't emit an event
      } else {
        lifecycleGateway.submit(new ClientErrorEvent("Connection to OBS failed: " + message));
      }
    });
    client.registerConnectCallback(response -> {
      log.debug("onConnect");
      if(settings.getPassword() != null) lifecycleGateway.submit(new ClientAuthenticatedEvent());
      lifecycleGateway.submit(new ClientConnectedEvent());
    });

    return new ObsClientDelegate(client, settings);

  }
}
