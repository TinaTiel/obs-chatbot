package com.tinatiel.obschatbot.core.action.enumerator;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.action.impl.ExecuteCommandAction;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.*;
import java.util.stream.Collectors;

public class ActionEnumeratorImpl implements ActionEnumerator {
    @Override
    public List<Action> enumerate(Command command, ActionContext context) throws CyclicalActionsException {

        // Check the command for cycles
        checkForCyclicalActions(command);

        // Attempt to list out all the actions sequenced for this execution
        try {
           List<Action> results = new ArrayList<>();
           enumerate(command, context, results);
           return results;
        } catch (StackOverflowError e) {
            // If a cycle occurs, wrap the error in a CyclicalActionsException
            throw new CyclicalActionsException(command, context, e);
        }
    }

    private void enumerate(Command command, ActionContext context, List<Action> results) {

        for(Action action:command.getActionSequencer().nextSequence()) {

            if(action instanceof ExecuteCommandAction) {
                ExecuteCommandAction castAction = (ExecuteCommandAction) action;
                results.addAll(
                        enumerate(castAction.getTarget(), context)
                );
            } else {
                results.add(action.createRunnableClone(context));
            }
        }

    }

    @Override
    public void checkForCyclicalActions(Command command) {
        try {
            List<Command> visited = new ArrayList<>();
            visited.add(command);
            checkForCyclicalActions(command, command, visited);
        } catch (CyclicalActionsException e) {
            throw e;
        }
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
