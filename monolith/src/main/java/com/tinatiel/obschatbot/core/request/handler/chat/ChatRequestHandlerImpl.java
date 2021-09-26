/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.handler.chat;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.data.command.CommandService;
import com.tinatiel.obschatbot.core.request.RequestContext;
import com.tinatiel.obschatbot.core.request.handler.CommandRequestDispatcher;
import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.core.user.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ${@link ChatRequestHandler} that delegates message parsing to a ${@link
 * ChatMessageParser}, looks up commands via ${@link CommandService}, builds a ${@link
 * RequestContext} by looking up the user via the ${@link UserService}, and then finally submits the
 * context and command to the ${@link CommandRequestDispatcher} for execution.
 */
public class ChatRequestHandlerImpl implements ChatRequestHandler {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final ChatMessageParser parser;
  private final CommandService commandService;
  private final UserService userService;
  private final CommandRequestDispatcher dispatcher;

  /**
   * Construct a new ChatRequestHandlerImpl instance.
   */
  public ChatRequestHandlerImpl(ChatMessageParser parser,
      CommandService commandService,
      UserService userService,
      CommandRequestDispatcher dispatcher) {
    this.parser = parser;
    this.commandService = commandService;
    this.userService = userService;
    this.dispatcher = dispatcher;
  }

  @Override
  public void handle(User partialUser, String message) {
    log.debug("Handling command from user " + partialUser + " with message " + message);

    // Check the message for a command request, parsing if present
    parser.parse(message)
        .ifPresent(result -> {

          // Find a command corresponding to the command invoked, if one exists
          Optional<Command> command = commandService.findByName(result.getCommandName());
          if (command.isPresent()) {

            // Lookup the full user, given what the chat client partially provided
            User fullUser = userService.getUserFromPartial(partialUser);

            // Submit the command and a new RequestContext (built from the full user)
            // for execution by the dispatcher.
            dispatcher.submit(
                command.get(),
                new RequestContext(fullUser, result.getArgs())
            );
          } else {
            // TODO log this in a hit/miss stat counter
            log.debug(String.format(
                "A command by name '%s' was requested, but none was found",
                result.getCommandName())
            );
          }
        });

  }

}
