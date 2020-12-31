/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch.expand;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.RunnableAction;
import com.tinatiel.obschatbot.core.dispatch.CommandRequestContext;
import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;

import java.util.List;

public interface CommandExpander {

    /**
     * Takes a command and expands it into a list of actions. The command should be checked
     * for cyclical actions before this is called, however in the worst case a wrapped StackOverFlow exception
     * will be thrown.
     * @param command Command to extract actions from
     * @return Flattened list of immutable actions containing the information needed to be run by an executor.
     * @throws CyclicalActionsException if the actions cause a cyclical reference. Also sets the cyclical flag on the command.
     */
    List<Action> expand(Command command) throws CyclicalActionsException;
    void checkForCyclicalActions(Command command) throws CyclicalActionsException;

}
