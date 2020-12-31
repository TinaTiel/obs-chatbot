/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.action.model;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.action.ActionType;

public class SendMessageAction implements Action<SendMessageAction> {

    private final ActionType ACTION_TYPE = ActionType.TWITCH_CHAT;
    private final String message;

    public SendMessageAction(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public ActionType getActionType() {
        return ACTION_TYPE;
    }

}
