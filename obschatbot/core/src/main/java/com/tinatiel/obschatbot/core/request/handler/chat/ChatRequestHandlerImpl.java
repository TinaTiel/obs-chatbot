/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.request.dispatch.CommandDispatcher;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.user.User;

public class ChatRequestHandlerImpl implements ChatRequestHandler {

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
        parser.parse(message)
                .ifPresent(result -> {
                    commandRepository.findByName(result.getCommandName())
                            .ifPresent(command -> {
                                dispatcher.submit(new RequestContext(
                                        user, result.getArgs()
                                ));
                            });
                });
    }

}
