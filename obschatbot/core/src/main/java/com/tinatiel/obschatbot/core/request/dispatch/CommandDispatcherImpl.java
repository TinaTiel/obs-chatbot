/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientNotRegisteredException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.Request;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.RequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandDispatcherImpl implements CommandDispatcher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RequestFactory requestFactory;
    private final CommandExecutorService commandExecutorService;

    public CommandDispatcherImpl(RequestFactory requestFactory, CommandExecutorService commandExecutorService) {
        this.requestFactory = requestFactory;
        this.commandExecutorService = commandExecutorService;
    }

    @Override
    public void submit(Command command, RequestContext requestContext) {
//        try {
//            Request request = requestFactory.build(command, requestContext);
//            commandExecutorService.submit(request);
//        } catch (CyclicalActionsException | ClientNotRegisteredException e) {
//            log.error(String.format("Not able to execute command %s with context %s", command, requestContext), e);
//        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
