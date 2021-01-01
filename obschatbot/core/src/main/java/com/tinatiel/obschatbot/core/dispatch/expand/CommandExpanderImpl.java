/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch.expand;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.model.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;

import java.util.*;
import java.util.stream.Collectors;

public class CommandExpanderImpl implements CommandExpander {

    @Override
    public List<Action> expand(Command command) throws CyclicalActionsException {

        // Check the command for cycles
        checkForCyclicalActions(command);

        // Attempt to list out all the actions sequenced for this execution
        try {
           List<Action> results = new ArrayList<>();
           enumerate(command, results);
           return results;
        } catch (StackOverflowError e) {
            // If a cycle occurs, wrap the error in a CyclicalActionsException
            throw new CyclicalActionsException(command, e);
        }
    }

    private void enumerate(Command command, List<Action> results) {

        for(Action action:command.getActionSequencer().nextSequence()) {

            if(action instanceof ExecuteCommandAction) {
                ExecuteCommandAction castAction = (ExecuteCommandAction) action;
                results.addAll(
                        expand(castAction.getTarget())
                );
            } else {
                results.add(action);
            }
        }

    }

    @Override
    public void checkForCyclicalActions(Command command) throws CyclicalActionsException {

        List<Command> visited = new ArrayList<>();
            visited.add(command);
            checkForCyclicalActions(command, command, visited);

    }

    private void checkForCyclicalActions(Command rootCommand, Command currentCommand, List<Command> visitedCommands) {

        List<Command> commands = currentCommand.getActionSequencer().listAll().stream()
                .filter(it -> it.getClass().equals(ExecuteCommandAction.class))
                .map(it -> (ExecuteCommandAction) it)
                .map(ExecuteCommandAction::getTarget)
                .collect(Collectors.toList());

        for(Command command:commands) {
            if(visitedCommands.contains(command)) {
                throw new CyclicalActionsException(rootCommand, visitedCommands);
            } else {
                visitedCommands.add(command);
            }
            checkForCyclicalActions(rootCommand, command, visitedCommands);
        }

    }

}
