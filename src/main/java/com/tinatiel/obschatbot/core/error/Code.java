/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

public enum Code {
    UNEXPECTED,
    NOT_ACCEPTABLE,
    SERVICE_NOT_READY,
    CYCLICAL_ACTION,
    CLIENT_NOT_AVAILABLE,
    CLIENT_BAD_CREDENTIALS,
    CLIENT_UNREACHABLE
}