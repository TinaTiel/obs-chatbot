/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.client.ActionClientFactory;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutor;
import com.tinatiel.obschatbot.core.request.dispatch.SequentialExecutorImpl;
import com.tinatiel.obschatbot.core.request.expand.CommandExpander;
import com.tinatiel.obschatbot.core.request.queue.ActionCommand;
import com.tinatiel.obschatbot.core.request.queue.MainQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RequestFactoryImpl implements RequestFactory {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CommandExpander commandExpander;
//    private final ActionClientFactory clientFactory;
    private final long commandTimeoutMs;
    private final MainQueue mainQueue;

    /**
     *
     * @param commandExpander Takes a command and expands it into a list of actions (including when actions execute other commands)
//     * @param clientFactory Fetches the client needed for a given Action implementation
     * @param commandTimeoutMs The maximum time a list of RunnableActions (a Command) has to execute. See com.tinatiel.obschatbot.core.request.Request
     */
    public RequestFactoryImpl(CommandExpander commandExpander, long commandTimeoutMs, MainQueue mainQueue) {
        if(commandExpander == null || mainQueue == null ) throw new IllegalArgumentException("arguments cannot be null");
        this.commandExpander = commandExpander;
//        this.clientFactory = clientFactory;
        this.commandTimeoutMs = commandTimeoutMs;
        this.mainQueue = mainQueue;
    }

    @Override
    public SequentialExecutor newSequentialExecutor() {
        return new SequentialExecutorImpl();
    }

    @Override
    public long getCommandTimeoutMs() {
        return commandTimeoutMs;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Request build(Command command, RequestContext context) throws CyclicalActionsException {
        if(command == null || context == null) throw new IllegalArgumentException("arguments cannot be null");
        log.debug("Building request from command " + command.getName() + " with context " + context);

        // sanitize inputs
        if(command == null || context == null) throw new IllegalArgumentException("arguments cannot be null");

        // Generate the list of runnable actions for the command
//        List<RunnableAction> runnableActions = commandExpander.expand(command).stream()
//                .map(action->
//                    action.createRunnableAction(clientFactory.getClient(action.acceptsClientType()), context)
//                ).collect(Collectors.toList());

        // Return the request
//        return new Request(
//                newSequentialExecutor(),
//                commandTimeoutMs,
//                runnableActions
//        );

        List<ActionCommand> actionCommands = commandExpander.expand(command).stream()
                .map(action -> new ActionCommand(action.acceptsClientType(), action, context))
                .collect(Collectors.toList());

        return new Request(mainQueue, commandTimeoutMs, actionCommands);
    }

}
