/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.dispatch.CommandExecutorService;
import com.tinatiel.obschatbot.core.request.expand.CommandExpander;

import java.util.List;
import java.util.stream.Collectors;

public class RequestFactoryImpl implements RequestFactory {

    private final CommandExpander commandExpander;
    private final ActionClientFactory clientFactory;
    private final CommandExecutorService commandExecutorService;

    public RequestFactoryImpl(CommandExpander commandExpander, ActionClientFactory clientFactory, CommandExecutorService commandExecutorService) {
        this.commandExpander = commandExpander;
        this.clientFactory = clientFactory;
        this.commandExecutorService = commandExecutorService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Request build(Command command, RequestContext context) throws CyclicalActionsException {

        // sanitize inputs
        if(command == null || context == null) throw new IllegalArgumentException("arguments cannot be null");

        // Generate the list of runnable actions for the command
        List<RunnableAction> runnableActions = commandExpander.expand(command).stream()
                .map(action->
                    action.createRunnableAction(clientFactory.getClient(action.acceptsClientType()), context)
                ).collect(Collectors.toList());

        // Return the request
        return new Request(
                commandExecutorService.newSequentialExecutor(),
                commandExecutorService.getRequestTimeoutMs(),
                runnableActions
        );
    }
}
