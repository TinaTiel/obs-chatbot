/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.remove.dispatch.CommandDispatcher;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ChatRequestHandlerImpl implements ChatRequestHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChatMessageParser parser;
    private final CommandRepository commandRepository;
    private final CommandDispatcher dispatcher;

    public ChatRequestHandlerImpl(ChatMessageParser parser, CommandRepository commandRepository, CommandDispatcher dispatcher) {
        this.parser = parser;
        this.commandRepository = commandRepository;
        this.dispatcher = dispatcher;
    }

    @Override
    public void handle(User user, String message) {
        log.debug("Handling command from user " + user + " with message " + message);
        parser.parse(message)
            .ifPresent(result -> {
                Optional<Command> command = commandRepository.findByName(result.getCommandName());
                if(command.isPresent()) {
                    dispatcher.submit(
                            command.get(),
                            new RequestContext(user, result.getArgs())
                    );
                } else {
                    log.debug(String.format("A command by name '%s' was requested, but none was found", result.getCommandName()));
                }
            });
    }

}
