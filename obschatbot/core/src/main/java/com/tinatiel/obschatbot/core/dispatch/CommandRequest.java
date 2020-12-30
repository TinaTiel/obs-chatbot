/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.core.dispatch;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.core.user.User;

import java.util.List;
import java.util.Objects;

public class CommandRequest {

    private final User user;
    private final Command command;
    private final List<String> arguments;

    public CommandRequest(User user, Command command, List<String> arguments) {
        this.user = user;
        this.command = command;
        this.arguments = arguments;
    }

    public User getUser() {
        return user;
    }

    public Command getCommand() {
        return command;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandRequest context = (CommandRequest) o;
        return Objects.equals(user, context.user) && Objects.equals(command, context.command) && Objects.equals(arguments, context.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, command, arguments);
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "command='" + command + '\'' +
                ", user='" + user + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
