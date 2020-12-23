package com.tinatiel.obschatbot.core.action.enumerator;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.action.ActionContext;
import com.tinatiel.obschatbot.core.command.Command;

import java.util.List;

public interface ActionEnumerator {

    List<Action> enumerate(Command command, ActionContext context) throws CyclicalActionsException;
    void checkForCyclicalActions(Command command);

}
