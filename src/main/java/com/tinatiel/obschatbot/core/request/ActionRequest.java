/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.messaging.AbstractObsChatbotEvent;

/**
 * Encapsulates an action to be completed by a compatible client, for example a
 * ${@link com.tinatiel.obschatbot.core.action.model.SendMessageAction} is an ActionRequest that
 * will be consumed/fulfilled by a chat client. Another example is an
 * ${@link com.tinatiel.obschatbot.core.action.model.ObsSourceVisibilityAction}, which contains the
 * information necessary for an OBS client to show or hide a Source item in OBS.
 */
public class ActionRequest extends AbstractObsChatbotEvent {

  private final Action action;
  private final RequestContext context;

  /**
   * Construct a new instance of an ActionRequest.
   *
   * @param context Context in which the action is to be completed. Some actions may need access
   *                to the request context, for example a SendMessage action that refers to the
   *                user executing the action.
   * @param action The action to execute. See the ${@link com.tinatiel.obschatbot.core.action.model}
   *               package for available implementations.
   */
  public ActionRequest(RequestContext context, Action action) {
    super();
    this.action = action;
    this.context = context;
  }

  public Action getAction() {
    return action;
  }

  public RequestContext getContext() {
    return context;
  }

  public boolean requiresCompletion() {
    return action.requiresCompletion();
  }

  @Override
  public String toString() {
    return "ActionRequest{"
      + super.toString()
      + "action=" + action
      + ", context=" + context
      + '}';
  }

}
