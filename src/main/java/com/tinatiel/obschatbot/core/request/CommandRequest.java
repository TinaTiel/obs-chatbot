/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a request to execute a command, encompassing the (1) ${@link RequestContext} and
 * (2) list of Actions to execute (converted into ${@link ActionRequest}s).
 */
public class CommandRequest extends AbstractObsChatbotEvent {

  private final RequestContext context;
  private final List<ActionRequest> actionRequests = new ArrayList<>();

  /**
   * Defines a runnable request, encompassing some run parameters and the actions/args themselves
   * (RunnableAction).
   *
   * @param actions The list of actions that will be run.
   */
  public CommandRequest(RequestContext context, List<Action> actions) {
    super();
    if (context == null || actions == null) {
      throw new IllegalArgumentException("context and actionRequests are required");
    }
    this.context = context;
    actions.forEach(action -> actionRequests.add(new ActionRequest(context, action)));
  }

  public RequestContext getContext() {
    return context;
  }

  public List<ActionRequest> getActionCommands() {
    return actionRequests;
  }

  @Override
  public String toString() {
    return "CommandRequest{"
      + super.toString()
      + ", context=" + context
      + ", actionCommands=" + actionRequests
      + '}';
  }
}
