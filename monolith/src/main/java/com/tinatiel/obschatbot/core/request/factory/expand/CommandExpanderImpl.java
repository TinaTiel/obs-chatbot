/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory.expand;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ${@link CommandExpander} that protects against unexpected (runtime-caused)
 * cyclical command expansion with a timeout.
 */
public class CommandExpanderImpl implements CommandExpander {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final long recursionTimeout;

  /**
   * Constructs a new instance.
   *
   * @param recursionTimeout how long to wait when expanding a command. Throws an
   *                         IllegalArgumentException is 0 or less. Generally speaking, 1000ms
   *                         should be more-than sufficient.
   */
  public CommandExpanderImpl(long recursionTimeout) {
    if (recursionTimeout <= 0) {
      throw new IllegalArgumentException("Recursion timeout must be greater than zero.");
    }
    this.recursionTimeout = recursionTimeout;
  }

  @Override
  public List<Action> expand(Command command) throws CyclicalActionsException {

    // Check the command for cycles
    checkForCyclicalActions(command);
    List<Action> results = new ArrayList<>();
    try {

      // Define a thread that will do the enumeration, so that a StackOverflow won't
      // bring the current thread to a halt, and we can set a timeout on it.
      executorService.submit(() -> {
        results.addAll(privateExpand(command));
      }).get(recursionTimeout, TimeUnit.MILLISECONDS);

    } catch (StackOverflowError | TimeoutException | InterruptedException e) {
      // If a cycle occurs or it times out, wrap the error in a CyclicalActionsException
      log.warn("Command '" + command.getName() + "' appears to have cyclical execution", e);
      throw new CyclicalActionsException(command, e);
    } catch (ExecutionException executionException) {
      log.info("Abandoned execution of enumerating command '" + command.getName() + "'. Cause: "
          + executionException.getCause());
    }

    return results;
  }

  private List<Action> privateExpand(Command command) {
    List<Action> results = new ArrayList<>();
    try {
      enumerate(command, results);
    } catch (InterruptedException ignore) {
      results.clear(); // Return nothing rather than partial result
    }
    return results;
  }

  private void enumerate(Command command, List<Action> results) throws InterruptedException {
    if (Thread.currentThread().isInterrupted()) {
      Thread.currentThread().interrupt();
      throw new InterruptedException(Thread.currentThread().getName() + " was interrupted");
    }
    for (Action action : command.getActionSequencer().nextSequence()) {

      if (action instanceof ExecuteCommandAction) {
        ExecuteCommandAction castAction = (ExecuteCommandAction) action;
        results.addAll(
            privateExpand(castAction.getTarget())
        );
      } else {
        results.add(action);
      }
    }

  }

  @Override
  public void checkForCyclicalActions(Command command) throws CyclicalActionsException {

    List<String> visited = new ArrayList<>();
    visited.add(command.getName());
    checkForCyclicalActions(command.getName(), command, visited);

  }

  private void checkForCyclicalActions(
      String rootCommand,
      Command currentCommand,
      List<String> visitedCommands) {

    // Find all of the ExecuteCommandAction commands of the current command, and collect
    // the Commands each targets.
    List<Command> commands = currentCommand.getActionSequencer().listAll().stream()
        .filter(it -> it.getClass().equals(ExecuteCommandAction.class))
        .map(it -> (ExecuteCommandAction) it)
        .map(ExecuteCommandAction::getTarget)
        .collect(Collectors.toList());

    // Iterate over the collected commands. If any of them have already been visited, then
    // a cycle has occurred.
    for (Command command : commands) {
      if (visitedCommands.contains(command.getName())) {
        throw CyclicalActionsException.fromNameAndBreadcrumbs(rootCommand, visitedCommands);
      } else {
        visitedCommands.add(command.getName());
      }
      checkForCyclicalActions(rootCommand, command, visitedCommands);
    }

  }

}
