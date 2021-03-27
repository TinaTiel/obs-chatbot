/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request.factory;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.error.ClientException;
import com.tinatiel.obschatbot.core.error.CyclicalActionsException;
import com.tinatiel.obschatbot.core.request.CommandRequest;
import com.tinatiel.obschatbot.core.request.RequestContext;

public interface RequestFactory {

    /**
     * Gets the maximum time a command will have to execute before it is interrupted.
     * @return milliseconds timeout
     */
    long getCommandTimeoutMs();

    CommandRequest build(Command command, RequestContext context) throws CyclicalActionsException, ClientException;

}
