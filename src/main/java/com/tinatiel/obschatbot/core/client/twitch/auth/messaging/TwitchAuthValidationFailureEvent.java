package com.tinatiel.obschatbot.core.client.twitch.auth.messaging;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class TwitchAuthValidationFailureEvent extends AbstractObsChatbotEvent {

  public TwitchAuthValidationFailureEvent()
  {
    super();
  }

  @Override
  public String toString() {
    return "TwitchAuthValidationFailureEvent{"
      + super.toString()
      + "}";
  }
}