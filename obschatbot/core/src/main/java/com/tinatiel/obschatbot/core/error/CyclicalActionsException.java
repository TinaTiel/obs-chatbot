/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.List;
import java.util.stream.Collectors;

public class CyclicalActionsException extends AbstractException {
    public CyclicalActionsException(String message, Throwable cause) {
        super(Code.CYCLICAL_ACTION, message, cause);
    }

    public CyclicalActionsException(Command parentCommand, CommandRequest context, Throwable cause) {
        this(
                "An unexpected infinite loop was detected on root command !" + parentCommand.getName()
                + " when run with context " + context,
                cause
        );
    }

    public CyclicalActionsException(Command parentCommand, List<Command> breadcrumbs) {
        this(
                "An infinite loop was detected on root command !" + parentCommand.getName()
                        + "; execution chain was: " + breadcrumbs.stream()
                            .map(it -> "!" + it.getName())
                            .collect(Collectors.joining(" -> "))
                        + " -> (loop back to !" + parentCommand.getName() + ")"
                , null
        );
    }

}
