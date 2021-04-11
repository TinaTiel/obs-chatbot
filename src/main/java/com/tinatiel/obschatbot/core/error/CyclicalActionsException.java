/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.error;

import com.tinatiel.obschatbot.core.command.Command;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An Exception that should be thrown if expanding a ${@link Command}'s actions could result
 * in cyclical expansion (a ${@link StackOverflowError} if otherwise unchecked).
 */
public class CyclicalActionsException extends AbstractCodedException {

  public CyclicalActionsException(String message, Throwable cause) {
    super(Code.CYCLICAL_ACTION, message, cause);
  }

  /**
   * Constructs a new instance in the case that unexpectedly a cycle occurred during
   * expansion of a Command (e.g. hopefully via expired timeout rather than StackOverflowError).
   *
   * @param parentCommand The parent command. Safely prints only the name of the command.
   * @param cause The cause, if applicable.
   */
  public CyclicalActionsException(Command parentCommand, Throwable cause) {
    this(
        "An unexpected infinite loop was detected on root command !" + parentCommand.getName(),
        cause
    );
  }

  /**
   * Construct a new instance during inspection of a Command's actions, providing a
   * list of Commands leading to the cyclical execution (like a stack trace).
   *
   * @param parentCommand The top-level / entrypoint Command that contains the cyclical execution.
   * @param breadcrumbs The list of Commands, in the order encountered, that led
   *                    to cyclical execution.
   */
  public CyclicalActionsException(Command parentCommand, List<Command> breadcrumbs) {
    this(
        "An infinite loop was detected on root command !" + parentCommand.getName()
        + "; execution chain was: " + breadcrumbs.stream()
        .map(it -> "!" + it.getName()).collect(Collectors.joining(" -> "))
        + " -> (loop back to !" + parentCommand.getName() + ")", null
    );
  }

}
