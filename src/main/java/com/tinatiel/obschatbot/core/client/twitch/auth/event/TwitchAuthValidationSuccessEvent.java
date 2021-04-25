package com.tinatiel.obschatbot.core.client.twitch.auth.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class TwitchAuthValidationSuccessEvent extends AbstractObsChatbotEvent {

  public TwitchAuthValidationSuccessEvent() {
    super();
  }

  @Override
  public String toString() {
    return "TwitchAuthValidationSuccessEvent{" + super.toString() + "}";
  }
}
