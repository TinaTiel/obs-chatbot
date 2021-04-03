package com.tinatiel.obschatbot.core;

/**
 * Wraps a client instance and the settings used to generate that client.
 * @param <C> Client instance type.
 * @param <S> Client settings type.
 */
public interface ClientInstanceWrapper<C, S> {
    C getClientInstance();
    S getSettings();
}
