/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch;

import com.tinatiel.obschatbot.core.user.User;

import java.util.List;
import java.util.Objects;

public class CommandRequestContext {

    private final User user;
    private final List<String> arguments;

    public CommandRequestContext(User user, List<String> arguments) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandRequestContext that = (CommandRequestContext) o;
        return Objects.equals(user, that.user) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, arguments);
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "user=" + user +
                ", arguments=" + arguments +
                '}';
    }
}
