package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.SendMessageAction;
import com.tinatiel.obschatbot.core.client.ActionCommandConsumer;
import com.tinatiel.obschatbot.core.client.event.ClientErrorEvent;
import com.tinatiel.obschatbot.core.client.twitch.chat.messaging.TwitchClientLifecycleGateway;
import com.tinatiel.obschatbot.core.request.ActionRequest;

public class TwitchChatActionCommandConsumer implements ActionCommandConsumer<TwitchChatClientDelegate> {

  private final TwitchClientLifecycleGateway stateClient;

  public TwitchChatActionCommandConsumer(
    TwitchClientLifecycleGateway stateClient) {
    this.stateClient = stateClient;
  }

  @Override
  public void consume(TwitchChatClientDelegate client, ActionRequest actionRequest) {
    try {
      Action action = actionRequest.getAction();
      if (action instanceof SendMessageAction) {
        client.sendMessage(((SendMessageAction) action).getMessage());
      }
    } catch (Exception unexpected) {
      stateClient.submit(new ClientErrorEvent(
        "Encountered unexpected exception while consuming " + actionRequest, unexpected
      ));
    }
  }
}
