/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.messaging.QueueClient;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRequestDispatcherImpl implements CommandRequestDispatcher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RequestFactory requestFactory;
    private final QueueClient<CommandRequest> commandRequestQueueClient;

    public CommandRequestDispatcherImpl(RequestFactory requestFactory, QueueClient<CommandRequest> commandRequestQueueClient) {
        if(requestFactory == null || commandRequestQueueClient == null) throw new IllegalArgumentException("arguments cannot be null");
        this.requestFactory = requestFactory;
        this.commandRequestQueueClient = commandRequestQueueClient;
    }

    @Override
    public void submit(Command command, RequestContext requestContext) {
        if(command == null || requestContext == null) throw new IllegalArgumentException("command and context are required");
        log.debug("Command " + command.getName() + " submitted with context " + requestContext);
        try {
            CommandRequest commandRequest = requestFactory.build(command, requestContext);
            commandRequestQueueClient.submit(commandRequest);
        } catch (CyclicalActionsException | ClientException e) {
            log.error(String.format("Not able to execute command %s with context %s",
                    command, requestContext), e);
        } catch (Exception unexpected) {
            log.error(String.format("Encountered unexpected exception while trying to execute command %s with context %s",
                    command, requestContext), unexpected);
        }
    }

}
