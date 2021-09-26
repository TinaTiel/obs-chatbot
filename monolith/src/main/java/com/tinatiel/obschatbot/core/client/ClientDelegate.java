package com.tinatiel.obschatbot.core.client;

/**
 * Represents a combination of client instance and settings used to instantiate the client
 * instance.
 *
 * @param <C> The client instance.
 * @param <S> The settings used to instantiate the client instance.
 */
public interface ClientDelegate<C, S> {

  C getClient();

  S getSettings();

}
