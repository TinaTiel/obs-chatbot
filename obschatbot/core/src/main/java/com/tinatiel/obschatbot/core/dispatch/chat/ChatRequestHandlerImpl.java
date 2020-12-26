package com.tinatiel.obschatbot.core.dispatch.chat;

import com.tinatiel.obschatbot.core.command.CommandRepository;
import com.tinatiel.obschatbot.core.dispatch.CommandDispatcher;
import com.tinatiel.obschatbot.core.dispatch.CommandRequest;
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
                                dispatcher.submit(new CommandRequest(
                                        user, command, result.getArgs()
                                ));
                            });
                });
    }

}
