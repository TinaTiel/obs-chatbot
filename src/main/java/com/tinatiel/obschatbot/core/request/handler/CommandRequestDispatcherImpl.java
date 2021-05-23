/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.CommandRequestFactory;
import com.tinatiel.obschatbot.core.request.messaging.CommandRequestGateway;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ${@link CommandRequestDispatcher} that passes a ${@link Command} and a ${@link
 * RequestContext} to a ${@link CommandRequestFactory} to build a ${@link CommandRequest} that is
 * passed to the CommandRequest Queue.
 */
@Slf4j
public class CommandRequestDispatcherImpl implements CommandRequestDispatcher {

  private final CommandRequestFactory commandRequestFactory;
  //  private final QueueClient<CommandRequest> commandRequestQueueClient;
  private final CommandRequestGateway commandRequestGateway;

  public CommandRequestDispatcherImpl(
      CommandRequestFactory commandRequestFactory,
      CommandRequestGateway commandRequestGateway) {
    if (commandRequestFactory == null || commandRequestGateway == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    this.commandRequestFactory = commandRequestFactory;
    this.commandRequestGateway = commandRequestGateway;
  }

  @Override
  public void submit(Command command, RequestContext requestContext) {

    if (command == null || requestContext == null) {
      throw new IllegalArgumentException("command and context are required");
    }
    log.debug("Command " + command.getName() + " submitted with context " + requestContext);

    try {
      CommandRequest commandRequest = commandRequestFactory.build(command, requestContext);
      commandRequestGateway.submit(commandRequest);
    } catch (CyclicalActionsException | ClientException e) {
      log.error(String.format(
          "Not able to execute command %s with context %s",
          command, requestContext), e);
    } catch (Exception unexpected) {
      log.error(String.format(
          "Encountered unexpected exception while trying to execute command %s with context %s",
          command, requestContext), unexpected);
    }
  }

}
