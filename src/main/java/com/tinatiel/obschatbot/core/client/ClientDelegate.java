package com.tinatiel.obschatbot.core.client;

public interface ClientDelegate<C, S> {

    C getClient();
    S getSettings();

}
