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

/**
 * Responsible for taking a raw ${@link Command} and ${@link RequestContext} and assembling them
 * into an executable ${@link CommandRequest}.
 */
public interface CommandRequestFactory {

  CommandRequest build(Command command, RequestContext context)
      throws CyclicalActionsException, ClientException;

}
