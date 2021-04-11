/*
 * Copyright (c) 2021 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.request;

import com.tinatiel.obschatbot.core.user.User;
import java.util.List;
import java.util.Objects;

/**
 * A value object that encompasses all elements of a unique command request for an user.
 */
public class RequestContext {

  private final User user;
  private final List<String> arguments;

  /**
   * Construct a new instance from an User and list of (String) arguments -- excluding the
   * command being requested. For example, the message "Howdy, !donate to our cause" executed by
   * a moderator would include the moderator's information and the arguments "to", "our", "cause".
   */
  public RequestContext(User user, List<String> arguments) {
    if (user == null || arguments == null) {
      throw new IllegalArgumentException("arguments cannot be null");
    }
    this.user = user;
    this.arguments = arguments;
  }

  public User getUser() {
    return user;
  }

  public List<String> getArguments() {
    return arguments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestContext that = (RequestContext) o;
    return Objects.equals(user, that.user) && Objects.equals(arguments, that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, arguments);
  }

  @Override
  public String toString() {
    return "RequestContext{"
      + "user=" + user
      + ", arguments=" + arguments
      + '}';
  }

}
