/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

public class ClientNotRegisteredException extends AbstractException {
    public ClientNotRegisteredException(String message, Throwable throwable) {
        super(Code.CLIENT_NOT_REGISTERED, message, throwable);
    }

    public ClientNotRegisteredException(String message) {
        super(Code.CLIENT_NOT_REGISTERED, message, null);
    }
}
