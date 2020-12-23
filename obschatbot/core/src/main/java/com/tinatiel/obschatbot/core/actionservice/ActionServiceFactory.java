package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.action.ActionType;

public interface ActionServiceFactory {

    ActionService getService(ActionType actionType);

}
