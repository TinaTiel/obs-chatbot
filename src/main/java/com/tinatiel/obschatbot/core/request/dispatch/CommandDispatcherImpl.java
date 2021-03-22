/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.Request;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandDispatcherImpl implements CommandDispatcher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RequestFactory requestFactory;
    private final CommandExecutorService commandExecutorService;

    public CommandDispatcherImpl(RequestFactory requestFactory, CommandExecutorService commandExecutorService) {
        if(requestFactory == null || commandExecutorService == null) throw new IllegalArgumentException("arguments cannot be null");
        this.requestFactory = requestFactory;
        this.commandExecutorService = commandExecutorService;
    }

    @Override
    public void submit(Command command, RequestContext requestContext) {
        if(command == null || requestContext == null) throw new IllegalArgumentException("command and context are required");
        log.debug("Command " + command.getName() + " submitted with context " + requestContext);
        try {
            Request request = requestFactory.build(command, requestContext);
            commandExecutorService.submit(request);
        } catch (CyclicalActionsException | ClientException e) {
            log.error(String.format("Not able to execute command %s with context %s",
                    command, requestContext), e);
        } catch (Exception unexpected) {
            log.error(String.format("Encountered unexpected exception while trying to execute command %s with context %s",
                    command, requestContext), unexpected);
        }
    }

    @Override
    public CommandExecutorService getUnderlyingService() {
        return commandExecutorService;
    }

}
