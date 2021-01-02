/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientNotRegisteredException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;

public interface RequestFactory {
    Request build(Command command, RequestContext context) throws CyclicalActionsException, ClientNotRegisteredException;
}
