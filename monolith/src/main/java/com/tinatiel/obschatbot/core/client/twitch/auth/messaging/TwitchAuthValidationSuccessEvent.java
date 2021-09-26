package com.tinatiel.obschatbot.core.client.twitch.auth.messaging;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Event representing a successful authentication message.
 */
public class TwitchAuthValidationSuccessEvent extends AbstractObsChatbotEvent {

  public TwitchAuthValidationSuccessEvent() {
    super();
  }

  @Override
  public String toString() {
    return "TwitchAuthValidationSuccessEvent{" + super.toString() + "}";
  }
}
