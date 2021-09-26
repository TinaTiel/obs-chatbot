package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.ActionRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * An ActionCommandConsumer that can consume actions intended for the Twitch Chat Client, e.g.
 * sending chat messages, banning, etc.
 */
@Slf4j
public class TwitchChatActionCommandConsumer implements
    ActionCommandConsumer<TwitchChatClientDelegate> {

  private final TwitchClientLifecycleGateway stateClient;

  public TwitchChatActionCommandConsumer(TwitchClientLifecycleGateway stateClient) {
    this.stateClient = stateClient;
  }

  @Override
  public void consume(TwitchChatClientDelegate client, ActionRequest actionRequest) {
    try {
      Action action = actionRequest.getAction();
      if (action instanceof SendMessageAction) {
        log.debug("Consuming ActionRequest: " + actionRequest);
        client.sendMessage(((SendMessageAction) action).getMessage());
      }
    } catch (Exception unexpected) {
      stateClient.submit(new ClientErrorEvent(
          "Encountered unexpected exception while consuming " + actionRequest, unexpected
      ));
    }
  }
}
