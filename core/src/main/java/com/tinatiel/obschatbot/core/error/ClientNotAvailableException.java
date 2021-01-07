/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

public class ClientNotAvailableException extends AbstractException {
    public ClientNotAvailableException(String message, Throwable throwable) {
        super(Code.CLIENT_NOT_AVAILABLE, message, throwable);
    }

    public ClientNotAvailableException(String message) {
        super(Code.CLIENT_NOT_AVAILABLE, message, null);
    }
}
