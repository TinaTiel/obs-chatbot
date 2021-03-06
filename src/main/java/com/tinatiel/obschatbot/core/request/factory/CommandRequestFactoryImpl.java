/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.factory.expand.CommandExpander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of CommandRequestFactory that delegates expanding a given Command into its
 * Actions via a ${@link CommandExpander} that checks for potentially cyclical execution.
 */
public class CommandRequestFactoryImpl implements CommandRequestFactory {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final CommandExpander commandExpander;

  /**
   * Creates a new instance of the RequestFactory.
   *
   * @param commandExpander Takes a command and expands it into a list of actions (including when
   *                        actions execute other commands)
   */
  public CommandRequestFactoryImpl(CommandExpander commandExpander) {
    if (commandExpander == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    this.commandExpander = commandExpander;
  }

  @SuppressWarnings("unchecked")
  @Override
  public CommandRequest build(Command command, RequestContext context)
      throws CyclicalActionsException {
    if (command == null || context == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    log.debug("Building request from command " + command.getName() + " with context " + context);

    // sanitize inputs
    if (command == null || context == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }

    // Expand the command into Actions and map into actionCommands

    return new CommandRequest(context, commandExpander.expand(command));
  }

}
