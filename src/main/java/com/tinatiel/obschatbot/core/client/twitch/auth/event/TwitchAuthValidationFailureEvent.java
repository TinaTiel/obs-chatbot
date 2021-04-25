package com.tinatiel.obschatbot.core.client.twitch.auth.event;

import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

public class TwitchAuthValidationFailureEvent extends AbstractObsChatbotEvent {

  private String reason;
  public TwitchAuthValidationFailureEvent(String reason)
  {
    super();
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "TwitchAuthValidationFailureEvent{"
      + super.toString()
      + "reason='" + reason + "', "
      + "}";
  }
}
