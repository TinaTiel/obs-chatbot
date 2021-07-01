package com.tinatiel.obschatbot.core.client.twitch.chat;

import com.tinatiel.obschatbot.core.client.ClientDelegate;
import org.pircbotx.PircBotX;

/**
 * A ClientDelegate pairing together the PircBotX IRC chatbot instance with the Twitch settings used
 * to instantiate it, and provides methods to interact directly with PircBotX (not meant to be used
 * directly, as the PircBotX instance should only be called when ready).
 */
public class TwitchChatClientDelegate implements
    ClientDelegate<PircBotX, TwitchChatClientSettings> {

  private final PircBotX delegate;
  private final TwitchChatClientSettings settings;

  public TwitchChatClientDelegate(PircBotX delegate, TwitchChatClientSettings settings) {
    this.delegate = delegate;
    this.settings = settings;
  }

  @Override
  public PircBotX getClient() {
    return delegate;
  }

  @Override
  public TwitchChatClientSettings getSettings() {
    return settings;
  }

  /**
   * Send the specified message to the joined channel.
   */
  public void sendMessage(String message) {
    delegate.sendIRC().message(
        "#" + settings.getBroadcasterAccountUsername(),
        message
    );
  }
}
