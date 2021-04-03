/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory.expand;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;

import java.util.List;

public interface CommandExpander {

    /**
     * Takes a command and expands it into a list of actions. The command should be checked
     * for cyclical actions before this is called, however in the worst case a wrapped StackOverFlow exception
     * will be thrown -- for example, caused by a TODO: Scripted Action
     * @param command Command to extract actions from
     * @return Flattened list of actions containing the information needed to be run by an executor.
     * @throws CyclicalActionsException if the actions cause a cyclical reference, may wrap a StackOverflowException.
     */
    List<Action> expand(Command command) throws CyclicalActionsException;

    /**
     * Statically checks for cyclical actions; e.g. if a Command contains Actions that point to a Command upstream in
     * the chain. Note, this does not catch any cycles caused by TODO: scripted Actions.
     * @param command Command to evaluate for cyclical actions
     * @throws CyclicalActionsException if a cycle is detected.
     */
    void checkForCyclicalActions(Command command) throws CyclicalActionsException;

}
