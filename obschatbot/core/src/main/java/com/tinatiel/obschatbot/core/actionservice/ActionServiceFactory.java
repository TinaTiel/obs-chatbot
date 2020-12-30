/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.actionservice;

import com.tinatiel.obschatbot.core.action.ActionType;

public interface ActionServiceFactory {

    ActionService getService(ActionType actionType);

}
