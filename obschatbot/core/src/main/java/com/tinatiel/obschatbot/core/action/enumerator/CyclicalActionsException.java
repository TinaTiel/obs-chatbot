package com.tinatiel.obschatbot.core.action.enumerator;

import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.List;
import java.util.stream.Collectors;

public class CyclicalActionsException extends RuntimeException {
    public CyclicalActionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CyclicalActionsException(Command parentCommand, CommandRequest context, Throwable cause) {
        super(
                "An unexpected infinite loop was detected on root command !" + parentCommand.getName()
                + " when run with context " + context,
                cause
        );
    }

    public CyclicalActionsException(Command parentCommand, List<Command> breadcrumbs) {
        super(
                "An infinite loop was detected on root command !" + parentCommand.getName()
                        + "; execution chain was: " + breadcrumbs.stream()
                            .map(it -> "!" + it.getName())
                            .collect(Collectors.joining(" -> "))
                        + " -> (loop back to !" + parentCommand.getName() + ")"
                , null
        );
    }

}
